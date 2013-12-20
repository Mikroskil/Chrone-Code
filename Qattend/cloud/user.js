module.exports = function() {
  var express = require('express');
  var app = express();

  app.get('/signup', function(req, res) {
    res.render('signup');
  });

  app.post('/signup', function(req, res) {
    var user = new Parse.User();
    user.set('name', req.body.name);
    user.set('username', req.body.username);
    user.set('email', req.body.email);
    user.set('password', req.body.password);

    user.signUp().then(function(user) {
      res.redirect('/');
    }, function(error) {
      res.render('signup', { flash: error.message });
    });
  });

  app.get('/signin', function(req, res) {
    res.render('signin');
  });

  app.post('/signin', function(req, res) {
    Parse.User.logIn(req.body.username, req.body.password).then(function(user) {
      res.redirect('/');
    }, function(error) {
      res.render('signin', { flash: error.message });
    });
  });

  app.post('/signout', function(req,res) {
    Parse.User.logOut();
    res.redirect('/');
  });

  return app;
}();
