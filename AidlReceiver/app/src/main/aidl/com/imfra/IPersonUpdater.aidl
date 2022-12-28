// IPersonUpdater.aidl
package com.imfra;
import com.imfra.Person;
// Declare any non-default types here with import statements

interface IPersonUpdater {

    void processPerson(in Person person, in int pid);
}