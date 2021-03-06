// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.service.rest.mock;

import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.daikon.properties.PropertiesImpl;

/**
 * Mock catastore properties for tests.
 */
public class MockDatastoreProperties extends PropertiesImpl implements DatastoreProperties {

    /**
     * Default constructor.
     * @param name the properties name.
     */
    public MockDatastoreProperties(String name) {
        super(name);
    }

}
