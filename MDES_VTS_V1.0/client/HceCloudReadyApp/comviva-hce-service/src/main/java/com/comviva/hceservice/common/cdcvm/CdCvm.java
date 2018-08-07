package com.comviva.hceservice.common.cdcvm;

/**
 * Consumer Device Card Verification Method (CDCVM).
 */
public class CdCvm {
    private boolean status;
    private Entity entity;
    private Type type;

    /**
     * Is CDCVM verification status
     * @return <code>true </code>CDCVM verified successfully <br>
     *     <code>false </code>CDCVM verification failed
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Set CDCVM verification status
     * @param status <code>true </code>CDCVM verified successfully <br>
     *     <code>false </code>CDCVM verification failed
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Returns Verifying entity.
     * @return Verifying entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Set Verifying entity.
     * @param entity Verifying entity
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * Get type of CDCVM.
     * @return CDCVM type
     */
    public Type getType() {
        return type;
    }

    /**
     * Set type of CDCVM.
     * @param type CDCVM type
     */
    public void setType(Type type) {
        this.type = type;
    }
}
