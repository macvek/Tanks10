var okienko = {
    pamiec: {},
    zindeks: 0,
    filtruj: function(input) {
        return input.replace(/;/g, " ").replace(/,/g, " ");
    },
    tabelaWynikow: new Array(),
    msgBox: function(message) {
        var html = "<div style=\"width:100%\"><div id=\"pytanie\">TANKS10</div>" +
                "<div id=\"tabela\" class=\"centrowanie\">" + message +
                "<br/><button class=\"apend\">OK</button></div></div>";

        okienko.wyswietl("msgBox", html, {});
    },
    wiadomoscFunc: function() {
        if ($(this).css("top") < -20)
            $(this).remove();
    },
    usunWiadomosc: function(node) {
        node.fadeOut(200, function() {
            $(this).remove();
        });
    },
    wiadomosc: function(name, body) {
        var msg = body.msg;

        var node = $("<div>").text(msg);
        $("#chat").append(node);
        setTimeout(function() {
            okienko.usunWiadomosc(node);
        }, 3000);
    },
    chatStatus: false,
    wyslijChat: function() {
        var wiadomosc = okienko.filtruj($("#chatInput").val());
        if (wiadomosc)
            window.socket.send(new TanksPacket("Say", {msg: wiadomosc}));

        okienko.zamknijChat();
    },
    otworzChat: function() {
        okienko.chatStatus = true;
        blaster.haltKeyboard = true;
        $("#chatInput").val("").css({display: "block"});
        $("#chatInput").focus();
    },
    zamknijChat: function() {
        $("#chatInput").blur().css({display: "none"});
        okienko.chatStatus = false;
        blaster.haltKeyboard = false;
    },
    ustaw: function(node) {
        var zindeks = okienko.zindeks++;
        node.css({zIndex: zindeks});
    },
    wczytaj: function(href, openHandlerName, closeHandlerName) {
        $.get(href, function(data) {
            okienko.wyswietl(href, data, {closeHandler: closeHandlerName, openHandler: openHandlerName});
        });
    },
    zapisanieDanych: function(name, body) {
        var tabelaDanych = okienko.tabelaWynikow;
        tabelaDanych.push({imie: body.imie, punkty: body.punkty, smierc: body.smierc, ping: body.ping});

    },
    wyswietlenieDanych: function(name, body) {
        $('#tabelka > tbody > tr').remove();
        var tabelaDanych = okienko.tabelaWynikow;

        var liczbaGraczy = tabelaDanych.length; // Liczebność zbioru.
        var i, x, k, j;

        // Sortujemy babelkowo

        for (var j = 0; j < liczbaGraczy - 1; j++) {
            for (var i = 0; i < liczbaGraczy - 1; i++) {
                if (tabelaDanych[i].punkty > tabelaDanych[i + 1].punkty)
                {

                    var xxx = tabelaDanych[i];
                    tabelaDanych[i] = tabelaDanych[i + 1];
                    tabelaDanych[i + 1] = xxx;
                }
            }
        }
        // Wyświetlamy wynik sortowania

        for (var k = 0; k < liczbaGraczy; k++) {
            var tr = $('<tr>');

            var td = $('<td>');
            td.text(k + 1);
            tr.append(td);

            var td = $('<td>');
            td.text(tabelaDanych[k].imie);
            tr.append(td);

            var td = $('<td>');
            td.text(tabelaDanych[k].punkty);
            tr.append(td);

            var td = $('<td>');
            td.text(tabelaDanych[k].smierc);
            tr.append(td);

            var td = $('<td>');
            td.text(tabelaDanych[k].ping);
            tr.append(td);

            $('#tabelka').append(tr);
        }


        okienko.tabelaWynikow = new Array();
    },
    wyswietl: function(href, html, obiekt) {
        if (okienko.pamiec[href]) {
            okienko.ustaw(okienko.pamiec[href]);
            return;
        }

        var node = $("<div>").html(html);		//zapamiętanie wartości html
        $("#okienka").append(node);				//wczytanie do okienka wartości html

        node.addClass("okienko");
        node.css({
            position: "absolute", //wyśrodkowanie okienka 
            left: ($(window).width() - $(node).width()) / 2,
            top: ($(window).height() - $(node).height()) / 2
        });

        $(".apend", node).click(function() {

            if (obiekt.closeHandler)
                window.okienko[obiekt.closeHandler](this);

            node.remove();
            okienko.pamiec[href] = null;

        });


        if (obiekt.openHandler)
            window.okienko[obiekt.openHandler](this);

        okienko.pamiec[href] = node;
        okienko.ustaw(node);
    }
};

$(document).ready(function() {
    $('#menu a').click(function() {
        var href = $(this).attr('href');
        if (!href)
            return;

        var closehandler = $(this).attr("closehandler");
        var openhandler = $(this).attr("openhandler");

        okienko.wczytaj(href, openhandler, closehandler);
        return false;
    });
});