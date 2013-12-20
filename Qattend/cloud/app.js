var express = require('express');
var expressLayouts = require('cloud/express-layouts');
var app = express();

// Global app configuration section
app.set('views', 'cloud/views');  // Specify the folder to find templates
app.set('view engine', 'ejs');    // Set the template engine
app.use(expressLayouts);          // Use the layout engine for express
app.use(express.bodyParser());    // Middleware for reading request body

// Register request handlers for each route
app.get('/', function(req, res) {
  res.render('hello', { message: 'Congrats, you just set up your app!' });
});

app.get('/users', function(req, res) {
  var query = new Parse.Query(Parse.User);
  query.select('name', 'username');
  query.find().then(function(objs) {
    res.render('users', { users: objs });
  });
});

app.get('/organizations', function(req, res) {
  var query = new Parse.Query(Parse.Object.extend('Organization'));
  query.select('name', 'username');
  query.find().then(function(objs) {
    res.render('organizations', { orgs: objs });
  });
});

// // Example reading from the request query string of an HTTP get request.
// app.get('/test', function(req, res) {
//   // GET http://example.parseapp.com/test?message=hello
//   res.send(req.query.message);
// });

// // Example reading from the request body of an HTTP post request.
// app.post('/test', function(req, res) {
//   // POST http://example.parseapp.com/test (with request body "message=hello")
//   res.send(req.body.message);
// });

// user.js controller
app.use('/', require('cloud/user'));

// Attach the Express app to Cloud Code.
app.listen();
