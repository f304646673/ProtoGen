package org.example;
import com.proto.message.gen.proto.Address;

public class AddressBuilder {
    public Address buildAddress(String city, String street, String door) {
        Address.Builder addressBuilder = Address.newBuilder();
        addressBuilder.setCity(city);
        addressBuilder.setStreet(street);
        addressBuilder.setDoor(door);
        return addressBuilder.build();
    }
}
