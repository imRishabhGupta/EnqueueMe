/**
 * UserController
 *
 * @description :: Server-side logic for managing users
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */

module.exports = {
	addQueue : function(req,res){
		User.findOne({devId : req.body.devId}).exec(function(err,val){
			if(val!=undefined){

			}else{
				req.body['code'] = ""
				User.create(req.body).exec(function(err,res){
					if(res){

					}
				});
			}
		});
	},
	Queue : function(req,res){
		User.findOne({deconId : req.body.deconId}).exec(function(err,val){
			if(val!=undefined){
				User.update({cQueue : req.body.current}).exec(function(err,val){
				
				});
			}else
				User.create(req.body).exec(function(err,res){
					if(res){

					}
				});
			}
		});
	}
};