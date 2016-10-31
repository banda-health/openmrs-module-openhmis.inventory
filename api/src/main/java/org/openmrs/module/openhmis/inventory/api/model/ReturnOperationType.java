/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api.model;

/**
 * The returned operation type is for items that get added back into the system as the result of a purchaser needing to
 * return the items. Only the items that are put back into stock should be part of this operation. Functionally it is the
 * same as a receipt operation, this type is mostly here for reporting purposes.
 */
public class ReturnOperationType extends ReceiptOperationType {

}
