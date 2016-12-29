/**
 * UserController
 *
 * @description :: Server-side logic for managing users
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */

module.exports = {
	addQueue : function(req,res){
		User.findOne({devId : req.body.devId}).exec(function(err,user){
			console.log(user);
			if(user!=undefined){
				return res.ok(null);
			}else{
				User.create(req.body).exec(function(err,create){
					if(res){
						updateQueue(req.body);
					}
				});
			}
		});
	},
	updateQueue : function(data){
		Queue.findOne({deconId : data.deconId}).exec(function(err,val){
			if(val!=undefined){
				console.log("already exixt");
			}else{
				data['cQueue'] = '5';
				Queue.create(data).exec(function(err,val){
					console.log(val);
				});
			}
		});
	},
	getQueue : function(req,res){
		console.log(req.params);
		User.find({devId : req.params.id}).exec(function(err,val){
			if(val.length>0) res.ok(val);
		});
	},
	removeQueue : function(req,res){
		User.destroy({devId : req.body.devId,beconId : req.body.beconId}).exec(function(err,val){
			if(val) res.ok(val);
		});
	}
};