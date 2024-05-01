package org.example;
import com.proto.message.gen.proto.Person;
import com.proto.message.gen.proto.Address;

public class PersonBuilder {
    public Person buildPerson(String name, int age) {
        AddressBuilder addressBuilder = new AddressBuilder();
        Person.Builder builder = Person.newBuilder();
        builder.setName(name);
        builder.setAge(age);
        builder.setWeight(70.5F);

        for (int i = 1; i <= 5; i++) {
            Address address = addressBuilder.buildAddress("city" + i, "street" + i, "door" + i);
            builder.addAddress(address);
        }
        return builder.build();
    }
}
