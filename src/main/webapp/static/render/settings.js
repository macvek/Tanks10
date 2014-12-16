Math.sin2 = function (a) {

    return Math.pow(Math.sin(a), 2);
}

Math.stopnie = function (a) {
    return a * Math.PI / 180;
}

alert = function (input) {
    console.log(input);
}

$(document).ready(function () {

    blaster.frontBuffer = document.getElementById('frontBuffer');
    blaster.backBuffer = document.getElementById('backBuffer');

    blaster.frontContext = blaster.frontBuffer.getContext("2d");
    blaster.backContext = blaster.backBuffer.getContext("2d");

    blaster.previousFrameTime = new Date().getTime();

    blaster.topObjects["cursor"] = new cursor();

    blaster.startTime = new Date().getTime();
    setInterval(function () {
        blaster.refresh();
    }, 10);	// 100/sekunde
    setInterval(function () {
        blaster.draw();
    }, 50);	// 20 fps

    $('#chat').mousemove(function (event) {
        var offset = $(this).offset();

        var x = event.pageX - offset.left;
        var y = event.pageY - offset.top;
        blaster.mousemove(x, y);
    })
            .click(function (event) {
                blaster.clicked();
            })
            .bind("contextmenu", function (e) {
                e.preventDefault();
                blaster.switchCursor();
            });

    $('html').keydown(function (key) {
        blaster.keydown(key);
    });

    $('html').keyup(function (key) {
        blaster.keyup(key);
    });

    $('html').bind("keydown", function (e) {
        switch (e.keyCode) {
            case 13:	// enter
            case 37: 	// strzalki
            case 38:
            case 39:
            case 40:
                e.preventDefault();
        }

    });

    $("#chatInput").focusout(function () {
        window.okienko.zamknijChat();
    });

});