package ru.my.scents.infra.grpc.client.middleware;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GrpcMetricsInterceptor implements ClientInterceptor {

    private static final String METRIC_GRPC_CLIENT_CALLS = "grpc.client.calls";

    private final MeterRegistry meterRegistry;
    private final String serviceName;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        ClientCall<ReqT, RespT> delegate = next.newCall(method, callOptions);
        return new MetricsClientCall<>(delegate, method);
    }

    class MetricsClientCall<ReqT, RespT> extends ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT> {

        private final MethodDescriptor<ReqT, RespT> method;
        private Timer.Sample timerSample;

        MetricsClientCall(ClientCall<ReqT, RespT> delegate, MethodDescriptor<ReqT, RespT> method) {
            super(delegate);
            this.method = method;
        }

        @Override
        public void start(Listener<RespT> responseListener, Metadata headers) {
            timerSample = Timer.start(meterRegistry);

            super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(responseListener) {

                @Override
                public void onClose(Status status, Metadata trailers) {
                    recordMetrics(status);
                    super.onClose(status, trailers);
                }
            }, headers);
        }

        private void recordMetrics(Status status) {
            if (timerSample != null) {
                String methodName = method.getFullMethodName();
                String statusCode = status.getCode().name();

                Timer timer = Timer.builder(METRIC_GRPC_CLIENT_CALLS)
                        .tag("service", serviceName)
                        .tag("method", methodName)
                        .tag("status", statusCode)
                        .tag("success", status.isOk() ? "true" : "false")
                        .register(meterRegistry);

                timerSample.stop(timer);
            }
        }
    }
}
