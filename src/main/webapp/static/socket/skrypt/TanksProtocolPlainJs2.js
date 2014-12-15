function TanksProtocolPlainJs2() {
    var UNSUPPORTED = "unsupported";

    this.listener = null;
    var END_OF_PACKET = ';';

    var arguments = [];

    function send(buffer, msg) {
        var sendFields = {};
        var name = encode(msg, sendFields);

        if (name === UNSUPPORTED) {
            return false;
        }

        var packet = name + ":";
        $.each(sendFields, function (key, v) {
            var value = formatValue(v);
            packet += key + "=" + value + ",";

        });
        packet = packet.substr(0, packet.length - 1);
        packet.append(END_OF_PACKET);
        packet.append("\r\n");

        buffer.buffered = packet;
        return true;
    }

    function formatValue(value) {
        return "" + value;
    }

    function decode(name, field) {
        arguments.push(name);
        $.each(field, function (i, v) {
            arguments.push(i);
            arguments.push("" + v);
        });

        arguments.add("\n");
    }

    function encode(input, field) {
        var messageParams = input.messageParams;
        var messageHead = input.messageHead;
        var messageName = input.messageName;

        var i = 0;
        while (true) {
            var fieldName = messageHead[i];
            if (fieldName === null) {
                break;
            }
            i++;

            var fieldValue = messageParams[fieldName];
            if (fieldValue === null) {
                continue;
            }

            field.put(fieldName, fieldValue);
        }

        return messageName;
    }

    function decodeEnd() {
        this.listener.onMessage(arguments);
        arguments = [];
    }

    function connectionLost() {

    }

    function receive(buffer) {
        var text = buffer.text;
        var cursor;
        while (buffer.mark < text.length) {
            cursor = buffer.mark;
            if (!findNextEnd(buffer)) {
                return;
            }

            parsePacket(text.substr(cursor, buffer.mark));
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

        var name = input.substr(0, colonIndex);
        if (name.length === 0) {
            return;
        }
        name = name.trim();
        var other = input.substr(colonIndex, input.length);
        var fields = other.split(",");

        for (var i = 0; i < fields.length; i++) {
            var attr = fields[i];
            var attrParts = attr.split("=");
            if (attrParts.length !== 2) {
                continue;
            }

            if (attrParts[0].length() === 0 || attrParts[1].length() === 0) {
                continue;
            }

            readFields[attrParts[0]] = attrParts[1];
        }

        decode(name, readFields);

    }

    function findNextEnd(buffer) {
        var text = buffer.text;
        var mark = buffer.mark;
        var position = text.substr(mark, text.length).indexOf(END_OF_PACKET)
        if (position === -1) {
            buffer.mark = buffer.text.length;
            return false;
        }
        else {
            buffer.mark = position;
            return true;
        }
    }

    this.send = send;
    this.receive = receive;
}