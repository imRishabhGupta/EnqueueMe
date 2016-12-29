/**
 * User.js
 *
 * @description :: TODO: You might write a short summary of how this model works and what it represents here.
 * @docs        :: http://sailsjs.org/documentation/concepts/models-and-orm/models
 */

module.exports = {

	attributes: {
		devId : {
			type : "STRING",
			required: true
		},
		beconId : {
			type : "STRING",
			required: true
		},
		location : {
			type : "STRING",
			required: true
		},
		queue :{
	      collection : "queue",
	      via : "devId"
	    }
	}
};

