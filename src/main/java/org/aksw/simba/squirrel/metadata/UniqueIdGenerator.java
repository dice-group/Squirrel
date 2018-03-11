package org.aksw.simba.squirrel.metadata;

import java.util.UUID;

public class UniqueIdGenerator {
    public static void main(String[] args) {
        UUID uid = UUID.randomUUID();
        String randomuid = uid.toString();
        System.out.println("UID=" + randomuid );
    }
}
