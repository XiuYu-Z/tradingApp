package persistence;

import persistence.exceptions.EntryDoesNotExistException;
import persistence.exceptions.EntryExistsException;
import persistence.exceptions.NonUniformObjectsException;

import java.io.IOException;
import java.util.List;


/**
 * The persistence interface is an abstraction that provides a persistence service,
 * whether it be to .csv, .ser, .json or database.
 */

public interface PersistenceInterface {

    /**
     * Returns one instance of records based on the id.
     *
     * @param id   the unique key of the record
     * @param type .class information about class T
     * @param <T>  the class type that we are querying
     * @return An object of class T
     * @throws IOException
     */
    <T> T get(int id, Class<T> type) throws IOException;

    /**
     * Gets a list of records that exist in a file/table based on the provided idList.
     *
     * @param idList a list of id's to be retrieved
     * @param type   .class information about class T
     * @param <T>    the class type of each element in ArrayList
     * @return An ArrayList of the records requested
     * @throws IOException throws this exception if there is a IO error.
     */
    <T> List<T> get(List<Integer> idList, Class<T> type) throws IOException;


    /**
     * Gets all records that exist in a file/table with the given key.
     *
     * @param type .class information about class T.
     * @param <T>  the class type of each element in ArrayList
     * @return An ArrayList of all records
     * @throws IOException throws this exception if there is a IO error.
     */
    <T> List<T> all(Class<T> type) throws IOException;


    /**
     * Saves new objects into persistence. Will not modify existing objects.
     * These objects must be of the same type and implement the Persistable interface.
     * If any one of the objects fail to be saved (due to, for example, a conflicting primary key), then none of them will be saved.
     * If any one of the objects have a primary key of 0, the gateway will automatically generate a primary key,
     * and save the object(s) with that primary key, and return an ArrayList of these objects
     * with the primary key of that object updated.
     *
     * @param newObjList an ArrayList of Persistable objects.
     * @param type       .class information about class T
     * @param <T>        the class type
     * @return A List of these objects with the primary key of that object updated.
     * @throws IOException                IOException
     * @throws NonUniformObjectsException throws this exception if the ArrayList consists of objects of different types.
     * @throws EntryExistsException       throws this exception if there exists a duplicate primary key.
     */
    <T extends Persistable> List<T> create(List<T> newObjList, Class<T> type) throws IOException, NonUniformObjectsException, EntryExistsException;


    /**
     * Saves one object into persistence.
     *
     * @param newObject a Persistable object.
     * @param type      .class information about class T
     * @param <T>       the class type
     * @return The object with primary key of that object updated.
     * @throws IOException                IOException
     * @throws NonUniformObjectsException throws this exception if the ArrayList consists of objects of different types.
     * @throws EntryExistsException       throws this exception if there exists a duplicate primary key.
     */
    <T extends Persistable> T create(T newObject, Class<T> type) throws IOException;


    /**
     * Updates one record with the same primary key in the current storage.
     *
     * @param updateObj one Persistable object.
     * @param type      .class information about class T
     * @param <T>       the class type
     * @return true if the update was successful
     * @throws IOException                throws this exception if there is a IO error.
     * @throws NonUniformObjectsException throws this exception if the ArrayList consists of objects of different types.
     * @throws EntryExistsException       throws this exception if there exists a duplicate primary keyin the provided list of objects to update.
     * @throws EntryDoesNotExistException throws this exception if an object's primary key does not exist in the current records.
     */
    <T extends Persistable> boolean update(T updateObj, Class<T> type) throws IOException;


    /**
     * Updates the records with the same primary key in the current storage.
     * Will keep records for primary keys which are not updated.
     * If the ArrayList provided consists of objects of different types, a NonUniformObjectsException will be thrown.
     * If a record is provided for which the primary key does not currently exist in storage, an EntryDoesNotExistException will be thrown.
     * If any two records in updateObjList has the same primary key, an EntryExistsException will be thrown.
     *
     * @param updateObjList an ArrayList of Persistable objects.
     * @param type          .class information about class T
     * @param <T>           the class type
     * @return true if the update was successful
     * @throws IOException                throws this exception if there is a IO error.
     * @throws NonUniformObjectsException throws this exception if the ArrayList consists of objects of different types.
     * @throws EntryExistsException       throws this exception if there exists a duplicate primary keyin the provided list of objects to update.
     * @throws EntryDoesNotExistException throws this exception if an object's primary key does not exist in the current records.
     */
    <T extends Persistable> boolean update(List<T> updateObjList, Class<T> type) throws IOException, NonUniformObjectsException, EntryExistsException, EntryDoesNotExistException;


    /**
     * Deletes a certain number of objects from persistence.
     *
     * @param idList a list of id's to be deleted
     * @param type   .class information about class T
     * @param <T>    the class type
     * @return true if the delete was successful
     * @throws IOException IOException
     */
    <T> boolean delete(List<Integer> idList, Class<T> type) throws IOException;


    /**
     * Removes all records relating to this key.
     *
     * @param type .class information about class T
     * @param <T>  the class type
     * @return true if the file exists and was successfully deleted, returns false otherwise
     */
    <T> boolean remove(Class<T> type);


}
