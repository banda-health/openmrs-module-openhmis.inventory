/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
openhmis.testData = {};
openhmis.testData.JSON = {
	itemCollection: '\
		{"results":[{\
			"uuid":"20aa2858-6642-4b7a-b456-b07621c26538",\
			"name":"Ciprofloxacin",\
			"codes":[\
				{\
					"uuid":"f546f45d-b000-4f9f-be93-1ec29aea0a8b",\
					"description":null,\
					"retired":false,\
					"retireReason":null,\
					"code":"P3",\
					"resourceVersion":"1.8"\
				},\
				{\
					"uuid":"4a37da9f-7e9d-4200-b518-8e1336ebaa96",\
					"description":null,\
					"retired":false,\
					"retireReason":null,\
					"code":"P1",\
					"resourceVersion":"1.8"\
				}\
			],\
			"department":{\
				"uuid":"faf2f364-189c-4959-9428-4f917f52b8de",\
				"name":"Pharmacy"\
			}\
		}]}',
	
	item: '{\
		"uuid":"20aa2858-6642-4b7a-b456-b07621c26538",\
		"name":"Ciprofloxacin",\
		"description":null,\
		"retired":false,\
		"retireReason":null,\
		"codes":[\
			{\
				"uuid":"f546f45d-b000-4f9f-be93-1ec29aea0a8b",\
				"code":"P3"\
			},\
			{\
				"uuid":"4a37da9f-7e9d-4200-b518-8e1336ebaa96",\
				"code":"P1"\
			}\
		],\
		"prices":[\
			{\
				"uuid":"1b81faeb-8380-4400-92d8-176f9282f06c",\
				"name":"AIDS",\
				"retired":false,\
				"price":1000.00\
			},\
			{\
				"uuid":"4c99f971-7d44-4bfc-acff-ad5d395c2e39",\
				"name":"Default",\
				"retired":false,\
				"price":4.40\
			}\
		],\
		"department":{\
			"uuid":"faf2f364-189c-4959-9428-4f917f52b8de",\
			"name":"Pharmacy"\
		},\
		"defaultPrice":{\
			"uuid":"1b81faeb-8380-4400-92d8-176f9282f06c",\
			"price":1000.00\
		},\
		"resourceVersion":"1.8"\
	}',
	
	departmentCollection: '{\
		"results":[\
			{\
				"uuid":"faf2f364-189c-4959-9428-4f917f52b8de",\
				"name":"Pharmacy",\
				"retired":false\
			},\
			{\
				"uuid":"f53f0242-b9df-4b56-8734-4e1c31903f96",\
				"name":"Lab",\
				"retired":false\
			}\
		],\
		"length":2\
	}'
}