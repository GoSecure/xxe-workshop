<?php


//test.php?func=phpinfo&val=1

$func = isset($_GET['func']) ? $_GET['func'] : "";
$val = isset($_GET['val']) ? $_GET['val'] : "";

function displayDate($format) {
	echo date($format);
}

function info($level) {
	phpinfo($level);
}

if($func == "") {
	echo "Select an option.<br/>";
	echo "<a href='?func=info&val=1'>Server info</a> | ";
        echo "<a href='?func=displayDate&val=F j, Y, g:i a'>Current date</a> ";
	echo "<br/><br/><img src='supercomputer.gif'>";

}
else {
	echo "<a href='?'>Back</a><br/><br/>";
	$func($val);
}
?>
