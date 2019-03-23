package org.springframework.samples.petclinic.owner;

import org.hibernate.dialect.Database;
import org.springframework.samples.petclinic.config.Sqlite;
import org.springframework.samples.petclinic.system.DatabaseToggles;

import java.util.ArrayList;
import java.util.Collection;

public class OwnerStorage {

    OwnerRepository ownerRepository;

    public OwnerStorage(OwnerRepository ownerRepository) {
        if(DatabaseToggles.isEnableNewDb) {
            System.out.println("New DB is running");
        }

        if(DatabaseToggles.isEnableOldDb) {
            this.ownerRepository = ownerRepository;
            System.out.println("Old Database is running");
        }

        if(DatabaseToggles.isUnderTest) {
            System.out.println("System is under test");
        }
    }


    public Collection<Owner> findByLastName(String lastName) {
        System.out.println("Shadow Read findByLastName");
        if(DatabaseToggles.isEnableOldDb && DatabaseToggles.isEnableNewDb) {
            Collection<Owner> expectedOwners = ownerRepository.findByLastName(lastName);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Collection<Owner> actualOwners = Sqlite.findByLastName(lastName);
                    //Consistency check stuff between expected and actual
                }
            }).start();
        }

        // If old database is enabled, return old values.
        if(DatabaseToggles.isEnableOldDb) {
            return ownerRepository.findByLastName(lastName);
        }

        return Sqlite.findByLastName(lastName);
    }

    public Owner findById(Integer ownerId) {
        System.out.println("Shadow Read findById");
        if(DatabaseToggles.isEnableOldDb && DatabaseToggles.isEnableNewDb) {
            Owner expectedOwner = ownerRepository.findById(ownerId);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Owner actualOwner = Sqlite.findOwnerById(ownerId);
                    //Consistency check stuff between expected and actual
                }
            }).start();
        }

        // If old database is enabled, return old values.
        if(DatabaseToggles.isEnableOldDb) {
            return ownerRepository.findById(ownerId);
        }

        return Sqlite.findOwnerById(ownerId);
    }

    public void save(Owner owner) {

    }
}
