<?php
$dbconn = pg_connect("host=ec2-23-21-94-137.compute-1.amazonaws.com port=5432 dbname=doeul6s0b6ek user=jdjkyvhhicfzea password=Hf6wiJcig0DBCIXF7yG_HPQ2zf sslmode=require options='--client_encoding=UTF8'") or die('Could not connect: ' . pg_last_error());
?>

<?php $q = pg_query($dbconn, "SELECT title from event");
while ($x = pg_fetch_assoc($q)): ?>
<div><?php echo $x['title']; ?></div>
<?php endwhile; ?>

<div class="row">
  <div class="col-lg-4">
    <img class="img-circle" data-src="holder.js/140x140" alt="Generic placeholder image">
    <h2>Heading</h2>
    <p>Donec sed odio dui. Etiam porta sem malesuada magna mollis euismod. Nullam id dolor id nibh ultricies vehicula ut id elit. Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Praesent commodo cursus magna.</p>
    <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p>
  </div>
  <div class="col-lg-4">
    <img class="img-circle" data-src="holder.js/140x140" alt="Generic placeholder image">
    <h2>Heading</h2>
    <p>Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem nec elit. Cras mattis consectetur purus sit amet fermentum. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh.</p>
    <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p>
  </div>
  <div class="col-lg-4">
    <img class="img-circle" data-src="holder.js/140x140" alt="Generic placeholder image">
    <h2>Heading</h2>
    <p>Donec sed odio dui. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Vestibulum id ligula porta felis euismod semper. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>
    <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p>
  </div>
</div>

<hr class="featurette-divider">

<div class="row featurette">
  <div class="col-md-7">
    <h2 class="featurette-heading">First featurette heading. <span class="text-muted">It'll blow your mind.</span></h2>
    <p class="lead">Donec ullamcorper nulla non metus auctor fringilla. Vestibulum id ligula porta felis euismod semper. Praesent commodo cursus magna, vel scelerisque nisl consectetur. Fusce dapibus, tellus ac cursus commodo.</p>
  </div>
  <div class="col-md-5">
    <img class="featurette-image img-responsive" data-src="holder.js/500x500/auto" alt="Generic placeholder image">
  </div>
</div>

<hr class="featurette-divider">

<div class="row featurette">
  <div class="col-md-5">
    <img class="featurette-image img-responsive" data-src="holder.js/500x500/auto" alt="Generic placeholder image">
  </div>
  <div class="col-md-7">
    <h2 class="featurette-heading">Oh yeah, it's that good. <span class="text-muted">See for yourself.</span></h2>
    <p class="lead">Donec ullamcorper nulla non metus auctor fringilla. Vestibulum id ligula porta felis euismod semper. Praesent commodo cursus magna, vel scelerisque nisl consectetur. Fusce dapibus, tellus ac cursus commodo.</p>
  </div>
</div>

<hr class="featurette-divider">

<div class="row featurette">
  <div class="col-md-7">
    <h2 class="featurette-heading">And lastly, this one. <span class="text-muted">Checkmate.</span></h2>
    <p class="lead">Donec ullamcorper nulla non metus auctor fringilla. Vestibulum id ligula porta felis euismod semper. Praesent commodo cursus magna, vel scelerisque nisl consectetur. Fusce dapibus, tellus ac cursus commodo.</p>
  </div>
  <div class="col-md-5">
    <img class="featurette-image img-responsive" data-src="holder.js/500x500/auto" alt="Generic placeholder image">
  </div>
</div>

<hr class="featurette-divider">
