package org.firebirdsql.jdbc;

import javax.resource.ResourceException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.jca.*;


public class InternalLocalTransaction extends FBLocalTransaction {

    public InternalLocalTransaction(FBManagedConnection mc, AbstractConnection c) {
        super(mc, c);
    }

    public void end() throws ResourceException {
        try {
            mc.end(getXid(), XAResource.TMSUCCESS);
            
            ((FBManagedConnectionFactory)mc.getManagedConnectionFactory()).forget(mc, getXid());
            
        } catch(XAException ex) {
            throw new FBResourceException(ex);
        } catch(GDSException ex) {
            throw new FBResourceException(ex);
        }
    }
}
