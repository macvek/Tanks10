function TanksProtocolPlainJs2() {
    var UNSUPPORTED = "unsupported";

    this.listener = null;
    this.send = send;
    this.receive = receive;

    var END_OF_PACKET = ';';

    var args = [];
    var self = this;

    function send(buffer, msg) {
        var sendFields = {};
        var name = encode(msg, sendFields);

        if (name === UNSUPPORTED) {
            return false;
        }

        var packet = name + ":";
        var stripLastChar = false;
        $.each(sendFields, function (key, v) {
            var value = formatValue(v);
            packet += key + "=" + value + ",";
            stripLastChar = true;

        });
        if (stripLastChar) {
            packet = packet.substring(0, packet.length - 1);
        }
        packet += END_OF_PACKET;
        packet += "\r\n";

        buffer.buffered = packet;
        return true;
    }

    function formatValue(value) {
        return "" + value;
    }

    function decode(name, field) {
        args.push(name);
        $.each(field, function (i, v) {
            args.push(i);
            args.push("" + v);
        });

        args.push("\n");
    }

    function encode(input, field) {
        var messageParams = input.messageParams;
        var messageHead = input.messageHead;
        var messageName = input.messageName;

        for (var i = 0; i < messageHead.length; i++) {
            var fieldName = messageHead[i];
            if (fieldName == null) {
                break;
            }

            var fieldValue = messageParams[fieldName];
            if (fieldValue == null) {
                continue;
            }

            field[fieldName] = fieldValue;
        }

        return messageName;
    }

    function decodeEnd() {
        self.listener.onMessage(args);
        args = [];
    }

    function connectionLost() {

    }

    function receive(buffer) {
        var text = buffer.text;
        var cursor;
        while (buffer.mark < text.length) {
            cursor = buffer.mark;
            if (!findNextEnd(buffer)) {
                break;
            }

            parsePacket(text.substring(cursor, buffer.mark));
            buffer.mark += 1;
        }

        decodeEnd();
        buffer.mark = cursor;
    }

    function parsePacket(input) {
        var readFields = {};
        var colonIndex = input.indexOf(":");
        if (colonIndex === -1) {
            return;
        }

        var name = input.substring(0, colonIndex);
        if (name.length === 0) {
            return;
        }
        name = name.trim();
        var other = input.substring(colonIndex+1, input.length);
        var fields = other.split(",");

        for (var i = 0; i < fields.length; i++) {
            var attr = fields[i];
            var attrParts = attr.split("=");
            if (attrParts.length !== 2) {
                continue;
            }

            if (attrParts[0].length === 0 || attrParts[1].length === 0) {
                continue;
            }

            readFields[attrParts[0]] = attrParts[1];
        }

        decode(name, readFields);

    }

    function findNextEnd(buffer) {
        var text = buffer.text;
        var mark = buffer.mark;

        var position = text.substring(mark, text.length).indexOf(END_OF_PACKET);
        if (position === -1) {
            buffer.mark = buffer.text.length;
            return false;
        }
        else {
            buffer.mark = mark + position;
            return true;
        }
    }
}