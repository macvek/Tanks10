<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html> 
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8" />
        <title>Tanks 10</title>
        <link type="text/css" href="panel/styl/tabelaWynikow.css" rel="stylesheet" />
        <link type="text/css" href="panel/styl/panel.css" rel="stylesheet" />
        <link type="text/css" href="panel/styl/menu.css" rel="stylesheet" />
        <script type="text/javascript" src="panel/skrypt/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="panel/skrypt/menu.js"></script>
        <script type="text/javascript" src="panel/skrypt/okienko.js"></script>

        <script type="text/javascript" src="render/blaster.js"></script>
        <script type="text/javascript" src="render/objects.js"></script>
        <script type="text/javascript" src="render/spawn.js"></script>
        <script type="text/javascript" src="render/entity.js"></script>

        <script type="text/javascript" src="socket/skrypt/socket.js"></script>
        <script type="text/javascript" src="socket/skrypt/init.js"></script>

    </head>
    <body>

        <script>
            function alert() {
            }
        </script>

        <style>
            #backBuffer {
                background-image: url('panel/grafika/welcome.png');	
            }
        </style>

        <div id="menu">
            <ul class="menu">
                <li id="graj"><a href="panel/menu/polacz.html" class="rodzic" onclose="polaczMenu"><span>Graj</span></a></li>
                <li><a><span>Menu Glowne</span></a>
                    <ul>
                        <li id="tabelaW"><a href="panel/menu/wyniki.html" onclose="closeWyniki" onopen="openWyniki"><span>Tabela wynikow</span></a></li> 

                        <li id="zmienImie"><a href="panel/menu/zmienImie.html" onopen="zmienImieEnter" onclose="zmienImieMenu"><span>Zmien imie</span></a></li>
                        <li id="zmienKolor"><a href="panel/menu/zmienKolor.html" onclose="zmienKolorMenu"><span>Zmien kolor</span></a></li>

                        <li id="komunikacja"><a href="panel/menu/komunikacja.html"><span>Sterowanie</span></a></li>
                    </ul>
                </li>
                <li><a><span>Menu Broni</span></a>
                    <ul>
                        <li id="tabelaBroni"><a href="panel/menu/tabelaBroni.html" onclose="zmienBronMenu"><span>Bron</span></a></li>
                        <li id="tabelaCzolgu"><a href="panel/menu/tabelaCzolgu.html" onclose="zmienCzolgMenu" ><span>Czolg</span></a></li>
                    </ul>
                </li>

                <li class="last"><a href="panel/menu/kontakt.html"><span>O nas</span></a></li>
            </ul>
        </div>

        <div id="okienka"></div>


        <div id="render" style="position:relative;width: 640px; margin: 0 auto; border:10px solid white;">
            <div id="chat"></div>
            <input type="text" id="chatInput"></input>
            <div id="pasekStanu" style="display:none;height:480px;background-color:rgba(255,255,255,0.2);position:absolute;top:0px;left:-200px;width: 200px;">
                <div id="energia" style="height:200px; font-family:verdana; font-size:80px; font-weight:bold;padding-left:15px;padding-top:10px;color:rgba(96,0,0,0.8);background-image:url(panel/grafika/hp.png);background-position:50px 10px; background-repeat:no-repeat;"></div>
                <button style="width:100px;height:50px;margin-left:50px;" onclick="blaster.suicide()">Samo zniszczenie!</button> 
            </div>
            <canvas style="margin:0;" id="backBuffer" width="640" height="480" ></canvas>
            <canvas style="display:none; margin:0;" id="frontBuffer" width="640" height="480" ></canvas>
        </div>
        <div class="debug">ServerFrame:<span id="serverFrame"></span></div>
        <div class="debug">ClientFrame:<span id="clientFrame"></span></div>
        <div class="debug">Offset:<span id="offset"></span></div>
        <pre class="debug" id="console" style="margin: 0 auto; color: white; padding:5px; background-color:#000000; width:640px; height:100px; overflow:auto; border:1px dotted white"></pre>

        <div id="stopka">Tanks10 TEAM <a href="http://apycom.com"></a></div>


        <script type="text/javascript" src="render/settings.js" ></script>
        <script>
                    $(document).ready(function() {
                        if (document.location.hash === "#debug")
                            $(".debug").css({display: "block"});
                    });
        </script>

    </body>
</html>