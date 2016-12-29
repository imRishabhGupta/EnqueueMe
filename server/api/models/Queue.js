/**
 * Queue.js
 *
 * @description :: TODO: You might write a short summary of how this model works and what it represents here.
 * @docs        :: http://sailsjs.org/documentation/concepts/models-and-orm/models
 */

module.exports = {
	attributes: {
		beconId : {
			type : "STRING",
			required: true
		},
		devId : {
			type : "STRING",
			required: true
		},
		userId :{
	      model:'user'
	    },
	   	number:{
	      collection : "quenumber",
	      via : "becon"
	    }
	}
};

