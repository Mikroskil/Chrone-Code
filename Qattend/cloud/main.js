require('cloud/app.js');

Parse.Cloud.define('users', function(req, res) {
  var query = new Parse.Query(Parse.User);
  query.select('name', 'username');
  query.find().then(function(objs) {
    res.success(objs);
  }, function(error) {
    res.error(error.message);
  });
});

Parse.Cloud.define('organizations', function(req, res) {
  var query = new Parse.Query('Organization');
  query.select('name', 'username');
  query.find().then(function(objs) {
    res.success(objs);
  }, function(error) {
    res.error(error.message);
  });
});

Parse.Cloud.define('events', function(req, res) {
  var query = new Parse.Query('Event');
  query.select('title', 'location', 'startDate', 'endDate');
  query.find().then(function(objs) {
    res.success(objs);
  }, function(error) {
    res.error(error.message);
  });
});

Parse.Cloud.beforeSave('Organization', function(req, res) {
  var query = new Parse.Query('Organization');
  query.equalTo('username', req.object.get('username'));
  query.first().then(function(obj) {
    if (obj) {
      res.error("The organization username has already been taken.");
    } else {
      res.success();
    }
  }, function(error) {
    res.error("Could not validate organization username uniqueness.");
  });
});

Parse.Cloud.beforeSave('_User', function(req, res) {
  if (req.user !== null && req.user.get('orgCount') > 3) {
    res.error("You already reach the limit of organization number.");
  } else {
    res.success();
  }
});
