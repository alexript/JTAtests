/*
 * Copyright 2023 malyshev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.napilnik.client.context;

import bitronix.tm.jndi.BitronixContext;
import bitronix.tm.resource.jms.PoolingConnectionFactory;
import java.util.HashMap;
import java.util.Map;
import javax.naming.Name;
import javax.naming.NamingException;

/**
 *
 * @author malyshev
 */
public class ClientContext extends BitronixContext {

    private static final Map<String, Object> BINDS = new HashMap<>();

    public ClientContext() {
        super();
    }

    @Override
    public Object lookup(String s) throws NamingException {
        if (BINDS.containsKey(s)) {
            return BINDS.get(s);
        }
        Object o = super.lookup(s);
        if (o instanceof PoolingConnectionFactory pcf) {
            return pcf.getXaConnectionFactory();
        }
        return o;
    }

    @Override
    public void bind(Name name, Object o) throws NamingException {
        bind(name.toString(), o);
    }

    @Override
    public void bind(String s, Object o) throws NamingException {
        BINDS.put(s, o);
    }
}
