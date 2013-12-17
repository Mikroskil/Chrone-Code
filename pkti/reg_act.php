<?php
include "db.php";
$username = addslashes(strip_tags ($_POST['username']));
$password = addslashes(strip_tags ($_POST['password']));
$confirm = addslashes(strip_tags ($_POST['confirm']));
$email = addslashes(strip_tags ($_POST['email']));
$event_name = addslashes(strip_tags ($_POST['event_name']));
$event_date = addslashes(strip_tags ($_POST['event_date']));
$event_month = addslashes(strip_tags ($_POST['event_month']));
$event_year = addslashes(strip_tags ($_POST['event_year']));
$event_desc = addslashes(strip_tags ($_POST['event_desc']));

//script ini untuk mengecek apakah form sudah terisi semua
if ($username&&$password&&$confirm&&$email&&$event_name&&$event_date&&$event_month&&$event_year) 
{
//berfunsgi untuk mengecek form tidak boleh lebih dari 10
	if (strlen($username)> 50)
	{
		echo "username tidak boleh lebih dari 50 karakter";
	}
	else 
	{
//password harus 6-25 karakter
		if (strlen($password)> 50 || strlen($confirm)<5)
		{
			echo "Password harus antara 5-50 karakter";
		}
		else 
		{
//untuk mengecek apakah form password dan form konfirmasi password sudah sama
			if ($password == $confirm)
			{
			$sql_get = mysql_query ("SELECT * FROM register WHERE username = '$username'");
			$num_row = mysql_num_rows($sql_get);
//fungsi script ini adalah untuk mengecek ketersediaan username, jika tidak tersedia maka program akan berjalan
				if ($num_row ==0) 
				{
					$password = md5($password);
					$confirm = md5($confirm);
					$sql_insert = mysql_query("INSERT INTO register VALUES ('$username','$password','$confirm','$email','$event_name','$event_date','$event_month','$event_year','$event_desc')");
					echo "Pendaftaran Berhasil dilakukan. Silahkan cek email anda untuk konfirmasi lebih lanjut";
				}
				else 
				{
					echo "Username sudah terdaftar";
				}
			}
			else 
			{
				echo "Password yang kamu masukan tidak sama!";
 
			}
 
		}
 
	}
 
}
 
else 
{
	echo "Tolong penuhi form pendaftaran!";
}
?>