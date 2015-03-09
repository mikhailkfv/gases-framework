<?php
	require 'mysqli.php';
	if($sql_error)
	{
		echo "Well, shit.";
		echo $sql_error;
		exit;
	}
	
	function download($statement)
	{
		if(!$statement->execute())
		{
			echo "MySQL error: Failed to get file table ({$statement->errno}) {$statement->error}";
			exit;
		}
		$statement->store_result();
		$statement->bind_result($name, $type, $mimetype, $data, $downloadHub, $allowDeepLinks);
		$validStatement = $statement->fetch();
		
		$referer = $_SERVER['HTTP_REFERER'];
		if($allowDeepLinks == "1" || (!empty($referer) && (stripos($referer, 'http://www.jamieswhiteshirt.com') === 0 || stripos($referer, 'http://jamieswhiteshirt.com') === 0 || stripos($referer, 'http://localhost') === 0)))
		{
			if($validStatement)
			{
				$filename = $name . "." . $type;
				header("Content-type: {$mimetype}");
				header("Content-Disposition: attachment; filename=\"{$filename}\"");
				echo $data;
				exit;
			}
		}
		else
		{
			if($validStatement)
			{
?>
<form id="redirect" action="<?php echo $downloadHub ? $downloadHub : "/" ?>" method="post">
	<input type="hidden" name="deepLinkPrevented" value="1" />
</form>
<script>
	document.getElementById("redirect").submit();
</script>
<?php
				exit;
			}
		}
	}
	
	if(isset($_GET['f']))
	{
		$fileid = $_GET['f'];
		
		$statement = $mysqli->prepare("UPDATE site_files SET uses=uses+1 WHERE id=?");
		$statement2 = $mysqli->prepare("SELECT site_files.name, site_files.type, site_files.mimetype, site_files.data, site_domains.downloadHub, site_domains.allowDeepLinks FROM site_files RIGHT JOIN site_domains ON site_domains.id=site_files.domain WHERE site_files.id=?");
		$statement->bind_param("i", $fileid);
		$statement2->bind_param("i", $fileid);
		if(!$statement->execute())
		{
			echo "MySQL error: Failed to increment file uses ({$statement->errno}) {$statement->error}";
			exit;
		}
		download($statement2);
	}
	else if(isset($_GET['p']))
	{
		$pointerid = $_GET['p'];
		
		$statement = $mysqli->prepare("UPDATE site_filepointers INNER JOIN site_files ON site_files.id=site_filepointers.pointer SET site_filepointers.uses=site_filepointers.uses+1, site_files.uses=site_files.uses+1 WHERE site_filepointers.id=?");
		$statement2 = $mysqli->prepare("SELECT site_files.name, site_files.type, site_files.mimetype, site_files.data, site_domains.downloadHub, site_domains.allowDeepLinks FROM site_filepointers LEFT JOIN site_files ON site_files.id=site_filepointers.pointer RIGHT JOIN site_domains ON site_domains.id=site_files.domain WHERE site_filepointers.id=?");
		$statement->bind_param("i", $pointerid);
		$statement2->bind_param("i", $pointerid);
		if(!$statement->execute())
		{
			echo "MySQL error: Failed to increment file uses ({$statement->errno}) {$statement->error}";
			exit;
		}
		download($statement2);
	}
?>

<html>
	<head>
		<title>Whoops!</title>
	</head>
	<body>
		<p>Whoops! It looks like you're trying to fetch a file which no longer exists, or it might have never existed at all!</p>
	</body>
</html>