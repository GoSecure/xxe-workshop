<html>
<head>
<title>Smart RSS Reader</title>

<link type="text/css" rel="stylesheet" href="styles.css"/>

</head>
<body>
<div class="container">
<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);



$httpScheme = strpos($_GET['url'],"http://") === 0 || strpos($_GET['url'],"https://") === 0;

if(!$httpScheme) die("Not a valid URL");

$content = file_get_contents($_GET['url']);

$doc = simplexml_load_string($content, NULL,
	LIBXML_NOENT);  //Pfff security is so overated

##print_r($doc);

echo "<h1>".htmlentities($doc->title)."</h1><br/>";
echo "<b>Description:</b><br><blockquote>".htmlentities($doc->subtitle)."</blockquote><br/>";

$items = $doc->entry;
for ($x=0; $x<=count($items); $x++) {
	if(isset($items[$x]->link) && isset($items[$x]->title) && isset($items[$x]->content)) {
		echo "<h3><a href='".htmlentities($items[$x]->link->href)."'>".htmlentities($items[$x]->title)."</a><br/></h3>";
		echo "<blockquote>".$items[$x]->content."</blockquote><br/>";
	}

}
?>
</div>

<!-- SVN revision 145 -->

</body>
</html>



