/**
 * UserController
 *
 * @description :: Server-side logic for managing users
 * @help        :: See http://sailsjs.org/#!/documentation/concepts/Controllers
 */

module.exports = {
	addQueue : function(req,res){
		User.findOne({devId : req.body.devId}).exec(function(err1,result){
			if(result){
				var data = {
					beconId : req.body.beconId,
					devId : req.body.devId,
					userId : result.id
				}
				Queue.create(data).exec(function(err,val2){
					if(val2){
						var final = {
							becon : val2.id
						}
						QueNumber.create(final).exec(function(err,val2){
							res.ok({success :true});
						});
					}
				});
			}else{
				User.create(req.body).exec(function(err2,create){
					if(create){
						var data = {
							beconId : req.body.beconId,
							devId : req.body.devId,
							userId : create.id
						}
						Queue.create(data).exec(function(err,val2){
							if(val2){
								var final = {
									becon : val2.id
								}
								QueNumber.create(final).exec(function(err,val2){
									res.ok({success :true});
								});
							}
						});
					}
				});
			}
		});
	},
	getQueue : function(req,res){
		User.findOne({devId : req.params.id}).populate("queues").exec(function(err,user){
			res.ok(user);
		});
	},
	getStatus : function(req,res){
		Queue.find({id : req.params.id}).populate('number').exec(function(err,user){
			res.ok(user);
		});
	},
	removeQueue : function(req,res){
		Queue.destroy({beconId : req.params.id}).exec(function(err,val){
			if(val) res.ok({success : true});
		});
	}
};