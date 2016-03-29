package com.myou.appback.modules.foundation.oid.util;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Title: 爱花族网站平台<br>
 * Description: 20位OID的 Hibernate生成器<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @version 1.0
 */
public class HibernateOIDGenerator implements IdentifierGenerator {

    public HibernateOIDGenerator() {

    }

    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        return  OIDGenerator.getOID(session.connection());
    }

}
