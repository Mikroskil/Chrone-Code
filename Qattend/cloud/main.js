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
    console.log(obj);
    if (obj) {
      res.error("The organization username has already been taken");
    } else {
      res.success();
    }
  }, function(error) {
    console.log(error.message);
    res.error("Could not validate organization username uniqueness");
  });
});

Parse.Cloud.define('createOrganization', function(req, res) {
  console.log(req.user.get('orgCount'));
  if (req.user.get('orgCount') < 3) {
    var org = new Parse.Object('Organization');
    org.save({
      name: req.params.name,
      username: req.params.username,
      ownBy: req.user
    }).then(function(objs) {
      req.user.increment('orgCount');
      req.user.save();
      console.log(objs);
      res.success("Organization created successfully");
    }, function(error) {
      console.log(error.message);
      res.error(error.message);
    });
  }
  else {
    res.error("You already reach the limit of organization number.");
  }
});
