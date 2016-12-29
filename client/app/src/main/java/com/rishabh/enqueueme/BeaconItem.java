package com.rishabh.enqueueme;


/**
 * Created by rohanagarwal94 on 29/12/16.
 */
public class BeaconItem {

    private String  namespaceId,instanceId;

    public BeaconItem() {
    }

    public BeaconItem(String namespaceId,String instanceId ) {
        this.instanceId=instanceId;
        this.namespaceId=namespaceId;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }


}
