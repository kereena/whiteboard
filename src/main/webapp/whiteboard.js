
var penDesc = "M25.31,2.872l-3.384-2.127c-0.854-0.536-1.979-0.278-2.517,0.576l-1.334,2.123l6.474,4.066l1.335-2.122C26.42,4.533,26.164,3.407,25.31,2.872zM6.555,21.786l6.474,4.066L23.581,9.054l-6.477-4.067L6.555,21.786zM5.566,26.952l-0.143,3.819l3.379-1.787l3.14-1.658l-6.246-3.925L5.566,26.952z";
var eraserDesc = "M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087 10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z";

var Drawing = {};
Drawing.NewID = function() {
    return "v" + (new Date().getTime());
};

Drawing.Path = function(app, elementID, color, data) {
    var path = app.paper.path(data);
    path.node.id = elementID;
    path.attr({stroke:color, 'stroke-width': 2});
    path.hover(
        function() {
            this.attr({stroke:'#ffa500', 'stroke-width': 5});
        },
        function() {
            this.attr({stroke:color, 'stroke-width':2});
        });
    path.click(function(){
        // this.s = new Tools.Selection(app, this);
        app.remove(elementID); this.remove();
    });
    return path;
};

var Tools = {};

/*
Tools.Selection = function(app, elem) {
    var self = this;

    self.app = app;
    self.elem = elem;

    var elem = self.elem.getBBox();

    self.background = self.app.paper.rect(0, 0, self.app.width, self.app.height).attr({'fill': '#000', opacity: 0.0})
        .click(function() {
            self.background.remove();
            self.rect.remove();
        });

    self.rect = self.app.paper.rect(elem.x, elem.y, elem.width, elem.height)
            .attr({'stroke-dasharray': '3,3', stroke: '#6495ED', 'stroke-width': 1, opacity: 1, cursor: 'move'});

    self.remove = function() {
        self.elem.s = null;
    }
}
*/

Tools.Pencil = function(app) {
    var self = this;
    self.app = app;
    self.m_pathArray;
    self.m_pathBox;
    self.start = function(x, y) {
        self.m_pathArray = [];
    };
    self.move = function (x, y) {
        if (self.m_pathArray.length == 0) {
            self.m_pathArray[0] = ["M", x, y];
            self.m_pathBox = Drawing.Path(self.app, Drawing.NewID(), self.app.color, self.m_pathArray);
        }
        else
            self.m_pathArray[self.m_pathArray.length] = ["L", x, y];

        self.m_pathBox.attr({path:self.m_pathArray});
    };
    self.up = function (x, y) {
        self.app.draw(self.m_pathBox.node.id, "path", self.m_pathArray);
    };
};

Tools.Line = function(app) {
    var self = this;
    self.app = app;
    self.m_start;
    self.m_end = null;
    self.m_pathBox;
    self.start = function(x, y) {
        self.m_end = null;
        self.m_start = ["M", x, y];
    };
    self.move = function (x, y) {
        if (self.m_end == null) {
            self.m_pathBox = Drawing.Path(self.app, Drawing.NewID(), self.app.color, [self.m_start]);
        }
        self.m_end = ["L", x, y];

        self.m_pathBox.attr({path:[self.m_start, self.m_end]});
    };
    self.up = function (x, y) {
        if (self.m_end != null)
            self.app.draw(self.m_pathBox.node.id, "path", [self.m_start, self.m_end]);
    };
};

function updatePen(x, y) {
    var pen = tool.mouse;
    var bbox = pen.getBBox();
    pen.translate(x - bbox.x, y - bbox.y - bbox.height);
    pen.toFront();
};

var App = {};

App.Controller = function(hashID, divID) {

    var self = this;

    var elem = document.getElementById(divID);
    self.element = $(elem);
    self.paper = new Raphael(elem);
    self.width = self.paper.canvas.clientWidth ? self.paper.canvas.clientWidth : self.paper.width;
    self.height = self.paper.canvas.clientHeight ? self.paper.canvas.clientHeight : self.paper.height;
    self.colors = {};
    self.color = '#000';

    console.log("width = " + self.width + ", height = " + self.height);

    self.board = self.paper.rect(0, 0, self.width, self.height).attr({fill: '#fff'});
    self.board.mousemove(function(event) {
        var evt = event;
        self.ox = evt.pageX - $(document).scrollLeft() - self.element.offset().left;
        self.oy = evt.pageY - $(document).scrollTop() - self.element.offset().top;
        // console.log("mousemove x=" + self.ox + ", y=" + self.oy);
        /*
        if (self.nextmove < new Date().getTime()) {
            self.move(self.ox, self.oy);
            self.nextmove = new Date().getTime() + 1000;
        }
        */
    });
    self.board.drag(function() {
        self.tool().move(self.ox, self.oy);
    }, function() {
        self.tool().start(self.ox, self.oy);
    }, function() {
        self.tool().up(self.ox, self.oy);
    });

    self.tool = function(newTool) {
        if (!newTool)
            return self.__tool;
        else
            return self.__tool = newTool;
    };

    self.draw = function(id, type, data) {
        var msg = JSON.stringify({
            elementID:   id,
            elementType: type,
            elementData: data
        });
        self.client.send("draw", msg);
    }

    self.remove = function(id) {
        var msg = JSON.stringify(id);
        self.client.send("remove", id);
    }

    self.move = function(x, y) {
        var msg = JSON.stringify({x: x, y: y});
        self.client.send("move", msg);
    }

    self.ondraw = function(data) {
        // elementID, elementType, elementData, username
        var color = self.colors[data.username];
        if (data.elementType == "path") {
            Drawing.Path(self, data.elementID, color, data.elementData);
        }
    }

    self.onjoin = function(data) {
        // connectionID, boardID, username, color
        self.colors[data.username] = data.color;
        if (data.username == self.username)
            self.color = data.color;
    }

    self.onremove = function(data) {
        $('#' + data).remove();
    }

    self.onmove = function(data) {
        console.log("moving: " + JSON.stringify(data));
    }
}


App.WhiteboardClient = function(app, hashID, username) {
    var self = this;

    self.app = app;
    self.hashID = hashID;
    self.username = username;

    var connURL = "ws://" + document.location.host + "/whiteboard?u=" + encodeURI(username) + "&t=" + encodeURI(hashID);

    self.ws = new WebSocket(connURL);
    self.ws.onclose = function() {
        alert('Connection to server closed, please reload page to reconnect!');
    };

    self.ws.onmessage = function(evt) {
        var message = evt.data;
        var action  = message.substr(0, message.indexOf(','));
        var payload = message.substring(message.indexOf(',') + 1);
        var payJson = JSON.parse(payload);
        if (!self.app[action])
            console.log("Unknown action: " + action + " for payload: " + payload);
        else
            self.app[action](payJson);
    };

    self.send = function(action, payload) {
        self.ws.send(action + "," + payload);
    };
}

function activateWhiteboard(username, hashID) {

    $('#welcome').hide();

    console.log("username=" + username + ", hash=" + hashID);

    app = new App.Controller(hashID, 'canvas');

    var pencil = new Tools.Pencil(app);
    var line  = new Tools.Line(app);

    app.tool(pencil);
    app.client = new App.WhiteboardClient(app, hashID, username);

    $('#line').click(function(){ app.tool(line); });
    $('#pencil').click(function(){ app.tool(pencil); });
}

function activateWelcome(username) {

    $('#whiteboard').hide();

    var createInput = $('#createInput');
    $.ajax({url: 'stories', dataType: 'json'}).done(function(data){
        for (var val in data) {
            $('<option>').text(data[val].storyTitle)
                .val(data[val].storyTitle)
                .data('description', data[val].storyDescription)
                .appendTo(createInput);
        }
    });

    $('#username').text(username);

    $('#createButton').click(function() {
        $.ajax({
            url: '/create',
            dataType: 'json',
            data: {'t': $('#createInput').val(), 'u': $('#username').text(), 'd': $('#createInput').val()}
        }).done(function(data) {
            var id = data.boardID;
            window.location.hash = '!' + id;
            activateWhiteboard($('#username').val(), encodeURI(id));
        });
    });
}

$(document).ready(function () {

    var username = localStorage.getItem("whiteboard-username") || Math.random().toString(36).substr(2, 6);
    var hashID = window.location.hash.substring(2);

    // if we have hashID then show whiteboard, otherwise show welcome screen
    if (hashID == "") {
        activateWelcome(username);
    }
    else {
        activateWhiteboard(username, hashID);
    }

});
