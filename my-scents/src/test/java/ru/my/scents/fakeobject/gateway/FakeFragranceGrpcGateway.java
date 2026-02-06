package ru.my.scents.fakeobject.gateway;

import java.util.Optional;
import ru.my.scents.boundary.gateway.FragranceGateway;
import ru.my.scents.domain.entity.fragrance.Fragrance;
import ru.my.scents.domain.entity.fragrance.FragranceID;
import ru.my.scents.stub.entity.FragranceStub;

public class FakeFragranceGrpcGateway implements FragranceGateway {

    @Override
    public Optional<Fragrance> findById(FragranceID userID) {

        if (userID == null) {
            return Optional.empty();
        }

        if (userID.equals(getOuterExistFragranceID())) {
            return Optional.of(FragranceStub.createFragranceWithId(userID.getValue().toString()));
        }

        return Optional.empty();
    }

    public static FragranceID getOuterExistFragranceID() {
        return FragranceID.of("fc044a50-339e-4683-977a-495cbe6f2cfe");
    }
}
