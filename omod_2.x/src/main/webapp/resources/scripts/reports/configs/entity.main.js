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
 *
 */

/* initialize and bootstrap application */
requirejs(['reports/configs/entity.module'], function() {
	angular.bootstrap(document, ['entitiesApp']);
});

//I don't know of any messages I need to add. Will these messages be the ones from messages.properties?
//Do I need to load them into emr to be able to access them on my pages?
// -AlanFiles
emr.loadMessages([
]);
