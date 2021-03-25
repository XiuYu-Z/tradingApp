package persistence;

import persistence.exceptions.EntryDoesNotExistException;
import persistence.exceptions.EntryExistsException;
import persistence.exceptions.NonUniformObjectsException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Persists to a ser file. Mimics the behaviour of a relational database.
 */
public class SerPersistenceGateway extends AbstractPersistenceGateway implements PersistenceInterface {


    /**
     * Returns one instance of records based on the id.
     *
     * @param id   the unique key of the record
     * @param type .class information about class T
     * @param <T>  the class type that we are querying
     * @return An object of class T
     * @throws IOException
     */
    @Override
    public <T> T get(int id, Class<T> type) throws IOException {
        List<Integer> idList = new ArrayList<>();
        idList.add(id);
        List<T> results = this.get(idList, type);
        if (results.size() > 0) return results.get(0);
        return null;
    }

    /**
     * Gets a list of records that exist in a file/table based on the provided idList.
     *
     * @param idList a list of id's to be retrieved
     * @param type   .class information about class T
     * @param <T>    the class type of each element in List
     * @return An List of the records requested
     * @throws IOException throws this exception if there is a IO error.
     */
    @Override
    public <T> List<T> get(List<Integer> idList, Class<T> type) throws IOException {

        List<T> all = this.all(type);
        List<T> result = new ArrayList<>();

        for (T t : all) {
            Persistable p = (Persistable) t; //We know it's persistable due to the way we save
            if (idList.contains(p.getKey())) {
                result.add(t);
            }
        }
        return result;

    }


    /**
     * Gets all records that exist in a file/table with the given key.
     * If a file has never been persisted before, and there is no .ser file, we return an empty ArrayList since
     * there are no entries to be found.
     *
     * @param type .class information about class T.
     * @param <T>  the class type of each element in List
     * @return An List of all records
     * @throws IOException throws this exception if there is a IO error.
     */
    @Override
    public <T> List<T> all(Class<T> type) throws IOException {

        List<T> result = new ArrayList<T>();
        try {
            List<Persistable> all = this.read(this.getFilePath(type.getName()));
            for (Persistable t : all) {
                result.add(type.cast(t));
            }
        } catch (FileNotFoundException e) {
            //Do nothing, since if there is no file that exists, we can return an empty list
        }

        return result;

    }


    /**
     * Saves new objects into persistence. Will not modify existing objects.
     * These objects must be of the same type and implement the Persistable interface.
     * If any one of the objects fail to be saved (due to, for example, a conflicting primary key), then none of them will be saved.
     * If any one of the objects have a primary key of 0, the gateway will automatically generate a primary key,
     * and save the object(s) with that primary key, and return an List of these objects
     * with the primary key of that object updated.
     *
     * @param newObjList a List of Persistable objects.
     * @param type       .class information about class T
     * @param <T>        the class type of each element in List
     * @return A List of these objects with the primary key of that object updated.
     * @throws IOException                IOException
     * @throws NonUniformObjectsException throws this exception if the ArrayList consists of objects of different types.
     * @throws EntryExistsException       throws this exception if there exists a duplicate primary key.
     */
    @Override
    public <T extends Persistable> List<T> create(List<T> newObjList, Class<T> type) throws IOException, NonUniformObjectsException, EntryExistsException {

        if (newObjList.size() == 0) return newObjList;

        //First check if duplicates exist in our provided ArrayList, except 0
        this.hasDuplicateKeys(newObjList);

        //Then check if everything is one type
        List<Persistable> existingList = null;
        try {
            existingList = this.read(this.getFilePath(type.getName()));
        } catch (FileNotFoundException e) {
            existingList = new ArrayList<>();
        }

        int newKey = this.getNextPrimaryKey(existingList, newObjList);
        //Now we assign non-zero keys
        for (Persistable p : newObjList) {
            if (p.getKey() == 0) {
                p.setKey(newKey);
                newKey++;
            }
        }

        //We need to check if everything passed in is of the same type (not including subclasses).
        //This is necessary since subclasses may require additional columns in a csv or in a relational database
        if (!this.containsOneType(newObjList, existingList)) {
            throw new NonUniformObjectsException();
        }

        List<Integer> existingIds = this.getListOfPrimaryKeys(existingList);
        for (Persistable p : newObjList) {
            if (existingIds.contains(p.getKey())) {
                throw new EntryExistsException();
            }
        }

        List<Persistable> newList = new ArrayList<>(newObjList);
        newList.addAll(existingList);

        this.write(newList, type.getName());

        return newObjList;

    }

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
    @Override
    public <T extends Persistable> T create(T newObject, Class<T> type) throws IOException {

        List<T> objectList = new ArrayList<>();
        objectList.add(newObject);
        List<T> resultList = this.create(objectList, type);
        return resultList.get(0);

    }


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
    @Override
    public <T extends Persistable> boolean update(T updateObj, Class<T> type) throws IOException {

        List<T> objectList = new ArrayList<>();
        objectList.add(updateObj);
        return this.update(objectList, type);

    }


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
     * @throws IOException                IOException
     * @throws NonUniformObjectsException throws this exception if the ArrayList consists of objects of different types.
     * @throws EntryExistsException       throws this exception if there exists a duplicate primary key in the provided list of objects to update.
     * @throws EntryDoesNotExistException throws this exception if an object's primary key does not exist in the current records.
     */
    @Override
    public <T extends Persistable> boolean update(List<T> updateObjList, Class<T> type) throws IOException, NonUniformObjectsException, EntryExistsException, EntryDoesNotExistException {

        if (updateObjList.size() == 0) return false;

        //First check if duplicates exist in our provided List
        this.hasDuplicateKeys(updateObjList);

        List<Persistable> existingList = null;
        try {
            existingList = this.read(this.getFilePath(type.getName()));
        } catch (FileNotFoundException e) {
            existingList = new ArrayList<>();
        }

        //Check we have only passed in one type of object.
        if (!this.containsOneType(updateObjList, existingList)) throw new NonUniformObjectsException();

        List<Integer> existingIds = this.getListOfPrimaryKeys(existingList); //Get existing id's
        List<Integer> updateIds = this.getListOfPrimaryKeys(updateObjList); //Get update id's
        List<Persistable> newObjList = new ArrayList<>();

        //Add updated objects to our new list
        for (Persistable p : updateObjList) {
            if (existingIds.contains(p.getKey())) newObjList.add(p);
            else throw new EntryDoesNotExistException();
        }

        //Add unchanged objects to our new list
        for (Persistable p : existingList) {
            if (!updateIds.contains(p.getKey())) newObjList.add(p);
        }

        //Save our new list
        this.write(newObjList, type.getName());

        return true;

    }


    /**
     * Deletes a certain number of objects from persistence.
     *
     * @param idList a list of id's to be deleted
     * @param type   .class information about class T
     * @param <T>    the class type
     * @return true if the delete was successful
     * @throws IOException
     */
    @Override
    public <T> boolean delete(List<Integer> idList, Class<T> type) throws IOException {

        List<Persistable> newObjList = new ArrayList<>();
        List<Persistable> all = this.read(this.getFilePath(type.getName()));
        for (Persistable p : all) {
            if (!idList.contains(p.getKey())) newObjList.add(p);
        }
        if (all.size() != newObjList.size()) {
            this.remove(type);
            if (newObjList.size() != 0) this.write(newObjList, type.getName());
            return true;
        }
        return false;

    }


    /**
     * Removes all records relating to this key.
     *
     * @param type .class information about class T
     * @param <T>  the class type
     * @return true if the file exists and was successfully deleted, returns false otherwise
     */
    @Override
    public <T> boolean remove(Class<T> type) {

        File file = new File(this.getFilePath(type.getName()));
        if (file.delete()) return true;
        return false;

    }

    /********************************************************************************************************
     *
     * Helper methods.
     *
     *********************************************************************************************************/


    private <S extends Persistable, T extends Persistable> int getNextPrimaryKey(List<S> existingList, List<T> newObjList) {

        int max = 1;
        for (Persistable p : existingList) {
            if (p.getKey() >= max) max = p.getKey() + 1;
        }
        for (Persistable p : newObjList) {
            if (p.getKey() >= max) max = p.getKey() + 1;
        }
        return max;

    }


    private List<Persistable> read(String filePath) throws IOException {

        ObjectInput input = null;
        List<Persistable> obj = null;
        try {
            InputStream file = new FileInputStream(filePath);
            InputStream buffer = new BufferedInputStream(file);
            input = new ObjectInputStream(buffer);
            obj = (List<Persistable>) input.readObject(); //We need this unchecked cast due to the way ser saves.

        } catch (ClassNotFoundException e) {
            System.out.println("Class is not found. Cannot load the class in through string name. " +
                    "Has the application changed since the data was last saved?");
            throw new IOException();
        } finally {
            if (input != null) {
                input.close();
            }
        }

        return obj;

    }


    private <T extends Persistable> boolean write(List<T> obj, String key) throws IOException {

        ObjectOutput output = null;

        try {
            String filePath = this.getFilePath(key);
            OutputStream file = new FileOutputStream(filePath);
            OutputStream buffer = new BufferedOutputStream(file);
            output = new ObjectOutputStream(buffer);
            output.writeObject(obj);
        } finally {
            if (output != null) {
                output.close();
            }
        }

        return true;
    }


    private String getFilePath(String fileName) {
        return "serfile" + fileName + ".ser";
    }


    private <S, T extends Persistable> boolean containsOneType(List<S> newList, List<T> existingList) {

        //We know that all objects in existing lists are the same type, so we only need to match the first of each list.
        if (newList.size() != 0 && existingList.size() != 0) {
            if (newList.get(0).getClass() != existingList.get(0).getClass()) {
                return false;
            }
        }

        //Next we check that all objects in newList are identical type.
        for (int i = 0; i < newList.size(); i++) {
            for (int j = i; j < newList.size(); j++) {
                if (newList.get(i).getClass() != newList.get(j).getClass()) {
                    return false;
                }
            }
        }

        return true;

    }


    private <T extends Persistable> boolean hasDuplicateKeys(List<T> persistableList) throws EntryExistsException {

        List<Integer> newIds = this.getListOfPrimaryKeys(persistableList);
        HashSet<Integer> s = new HashSet<Integer>();
        for (Integer i : newIds) {
            if (i != 0 && !s.add(i)) {
                throw new EntryExistsException();
            }
        }
        return false;
    }


    private <T extends Persistable> List<Integer> getListOfPrimaryKeys(List<T> persistableList) {

        List<Integer> keys = new ArrayList<Integer>();
        //We need to load the existing file, and loop to check that we don't override any entries
        for (Persistable p : persistableList) {
            keys.add(p.getKey());
        }
        return keys;

    }


}
