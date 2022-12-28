// IAidlDemonstration.aidl
package com.imfra;
import com.imfra.IPersonUpdater;
// Declare any non-default types here with import statements

interface IAidlDemonstration {

    void register(in IPersonUpdater personUpdater);
    void unregister();
}