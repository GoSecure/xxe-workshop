<html>
<head>
    <title>IMAGinE Converter</title>

    <script src="js/jquery.min.js"></script>

    <script>
        function convertAndDisplay() {
            var svgData = document.svg_form.svg_data.value;

            document.getElementById("image_generated").innerHTML="<img src='/convertImage.action?svg="+escape(svgData)+"'>";

        }
    </script>

</head>
<body>

<center>

<h1>IMAGinE Converter</h1>
<i>Simple and easy image conversion.</i>
<br><br>