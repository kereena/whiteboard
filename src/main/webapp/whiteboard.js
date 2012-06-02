
// add tooltip to Raphael.
// taken from: http://www.strathausen.eu/en/2010/04/25/raphael-svg-tooltip/
Raphael.el.tooltip = function (text) {

    this.tp = text;
    this.tp.ox = - text.getBBox().width - text.getBBox().x - 10;
    this.tp.oy = text.getBBox().height + text.getBBox().y;
    this.tp.hide();
    this.showTip = function(event) {
        this.mousemove(function(event){
            this.tp.translate(event.clientX -
                this.tp.ox,event.clientY - this.tp.oy);
            this.tp.ox = event.clientX;
            this.tp.oy = event.clientY;
        });
        this.tp.attr({text: this.data('username')});
        this.tp.show().toFront();
    }
    this.hideTip = function(event) {
        if (this.tp)
            this.tp.hide();
        this.unmousemove();
    };
    return this;
};

var Drawing = {};
Drawing.NewID = function() {
    return "v" + (new Date().getTime());
};

Drawing.Remove = function(element) {
    if (element.g)
        element.g.remove();
    if (element.hideTip)
        element.hideTip();
    element.remove();
}

Drawing.Snapshot = function(app) {
    noty({
        text: "Snapshot taken.",
        layout:"top",
        type:"success",
        animateOpen:{height:"toggle"},animateClose:{height:"toggle"},speed:500,timeout:5000,closeButton:false,closeOnSelfClick:true,closeOnSelfOver:false,modal:false});
}

Drawing.RichElement = function(app, element, elementID, username) {

    element.node.id = elementID;
    element.data('username', username);
    element.tooltip(app.tooltip);
    element.hover(function() {
        this.g = this.glow({color: '#ff0', width: 100});
        this.showTip();
    }, function() {
        if (this.g)
            this.g.remove();
        this.hideTip();
    });
    /*
    element.click(function() {
        app.remove(element.node.id);
        Drawing.Remove(element);
    })
    */

}

var Tools = {};

// Convert raphael element to Javascript object.
Tools.ToJSON = function(element) {
    var res = {'type': element.type};
    $.extend(res, element.attrs);
    return res;
}

Tools.ToSvg = function(elemID) {
    var svg = $('<div>').append($('#' + elemID).clone()).html();
    return svg;
}

Tools.Eraser = function(app) {
    var self = this;
    self.m_end = null;
    self.box = null;
    self.start = function(x, y) {
        self.box = null;
        self.m_start = {x: x, y: y};
    }
    self.move = function(x, y) {
        var offset = { x: Math.min(x, self.m_start.x), y: Math.min(y, self.m_start.y) };
        var dim = { width: Math.max(x, self.m_start.x) - offset.x, height: Math.max(y, self.m_start.y) - offset.y };

        if (self.box == null) {
            self.box = app.paper.rect(offset.x, offset.y, dim.width, dim.height).attr({stroke: '#fff', 'stroke-width': 2, fill: '#000', opacity: 0.3});
        }
        self.box.attr($.extend(offset, dim));
    }

    self.overlapping = function(sb, bb) {
        return !(sb.x > bb.x + bb.width|| bb.x > sb.x + sb.width ||
            sb.y > bb.y + bb.height || bb.y > sb.y + sb.height);
    }
    self.up = function(x, y) {
        if (self.box != null) {
            var sb = self.box.getBBox();
            var removed = [];
            app.paper.forEach(function(el) {
                if (el !== self.box) {
                    if (self.overlapping(sb, el.getBBox()))
                        removed.push(el);
                }
            });
            $.each(removed, function(idx, el) {
                app.remove(el.node.id);
                Drawing.Remove(el);
            });
            self.box.remove();
        }
        else {
            var elem = app.paper.getElementByPoint(self.m_start.x, self.m_start.y);
            if (elem) {
                app.remove(elem.node.id);
                Drawing.Remove(elem);
            }
        }
    }
}

Tools.Rectangle = function(app) {
    var self = this;
    self.box = null;
    self.start = function(x, y) {
        self.box = null;
        self.m_start = {x: x, y: y};
    }
    self.move = function(x, y) {
        var offset = { x: Math.min(x, self.m_start.x), y: Math.min(y, self.m_start.y) };
        var dim = { width: Math.max(x, self.m_start.x) - offset.x, height: Math.max(y, self.m_start.y) - offset.y };
        if (self.box == null) {
            self.box = app.paper.rect(offset.x, offset.y, dim.width, dim.height)
                .attr({stroke: app.color(), 'stroke-width': 2, fill: app.color(), 'fill-opacity': 0.2});
        }
        self.box.attr($.extend(offset, dim));
    }
    self.up = function(x, y) {
        if (self.box != null) {
            Drawing.RichElement(app, self.box, Drawing.NewID(), app.username);
            app.draw(self.box);
        }
    }
}

Tools.Pencil = function(app) {
    var self = this;
    self.m_pathArray;
    self.m_pathBox;
    self.start = function(x, y) {
        self.m_pathArray = [];
    };
    self.move = function (x, y) {
        if (self.m_pathArray.length == 0) {
            self.m_pathArray[0] = ["M", x, y];
            self.m_pathBox = app.paper.path(self.m_pathArray).attr({'stroke': app.color(), 'stroke-width': 2})
        }
        else
            self.m_pathArray[self.m_pathArray.length] = ["L", x, y];

        self.m_pathBox.attr({path:self.m_pathArray});
    };
    self.up = function (x, y) {
        if (self.m_pathArray.length > 0) {
            Drawing.RichElement(app, self.m_pathBox, Drawing.NewID(), app.username);
            app.draw(self.m_pathBox);
        }
    };
};

Tools.Line = function(app) {
    var self = this;
    self.m_start;
    self.m_end = null;
    self.m_pathBox;
    self.start = function(x, y) {
        self.m_end = null;
        self.m_start = ["M", x, y];
    };
    self.move = function (x, y) {
        if (self.m_end == null) {
            self.m_pathBox = app.paper.path([self.m_start]).attr({'stroke': app.color(), 'stroke-width': 2})
        }
        self.m_end = ["L", x, y];

        self.m_pathBox.attr({path:[self.m_start, self.m_end]});
    };
    self.up = function (x, y) {
        if (self.m_end != null) {
            Drawing.RichElement(app, self.m_pathBox, Drawing.NewID(), app.username);
            app.draw(self.m_pathBox);
        }
    };
};

Tools.Text = function(app, text) {
    var self = this;
    self.elem = null;
    self.start = function(x, y) {
        self.elem = null;
    }
    self.move = function(x, y) {
        if (self.elem == null)
            self.elem = app.paper.text(x, y, text).attr({fill: app.color()});

        self.elem.attr({x: x, y: y});
    }
    self.up = function(x, y) {
        if (self.elem != null) {
            Drawing.RichElement(app, self.elem, Drawing.NewID(), app.username);
            app.draw(self.elem);
        }
    }

}

Tools.Image = function(app, src) {
    var self = this;
    self.image = null;
    self.start = function(x, y) {
        self.image = null;
        self.m_start = {x: x, y: y};
    };
    self.move = function (x, y) {
        var offset = { x: Math.min(x, self.m_start.x), y: Math.min(y, self.m_start.y) };
        var dim = { width: Math.max(x, self.m_start.x) - offset.x, height: Math.max(y, self.m_start.y) - offset.y };

        if (self.image == null) {
            self.image = app.paper.image(src, offset.x, offset.y, dim.width, dim.height);
            self.image.node.id = Drawing.NewID();
        }

        self.image.attr($.extend(offset, dim));
    };
    self.up = function (x, y) {
        if (self.image != null) {
            Drawing.RichElement(app, self.image, Drawing.NewID(), app.username);
            app.draw(self.image);
        }
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

    self.tooltip = self.paper.text(0, 0, "tooltip").hide();

    console.log("width = " + self.width + ", height = " + self.height);

    // self.board = self.paper.rect(0, 0, self.width, self.height).attr({fill: '#fff'});
    self.element.mousemove(function(event) {
        var evt = event;
        self.ox = evt.pageX - $(document).scrollLeft() - self.element.offset().left;
        self.oy = evt.pageY - $(document).scrollTop() - self.element.offset().top;

        if (self.drawing)
            self.tool().move(self.ox, self.oy);
        // console.log("mousemove x=" + self.ox + ", y=" + self.oy);
        if (self.nextmove < new Date().getTime()) {
            self.move(self.ox, self.oy);
            self.nextmove = new Date().getTime() + 1000;
        }
    });
    self.element.mousedown(function(event) {
        self.drawing = true;
        self.modalDrawing = self.paper.rect(0, 0, self.width, self.height).attr({fill: '#fff', opacity: 0.0});
        self.tool().start(self.ox, self.oy);
    });
    self.element.mouseup(function(event) {
        if (self.drawing) {
            self.modalDrawing.remove();
            self.drawing = false;
            self.tool().up(self.ox, self.oy);
        }
    });

    self.__color = '#000';
    self.color = function(newColor) {
        if (!newColor)
            return self.__color;
        else
            return self.__color = newColor;
    }

    self.tool = function(newTool) {
        if (!newTool)
            return self.__tool;
        else
            return self.__tool = newTool;
    };

    self.draw = function(element) {
        var elementJson = Tools.ToJSON(element);
        var msg = JSON.stringify({
            elementID:   element.node.id,
            elementData: elementJson
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
        var json = data.elementData;
        if (!json.type)
            return;
        // elementID, elementType, elementData, username
        var color = self.colors[data.username];
        var element = self.paper[json.type]().attr(json);
        // fixup element
        Drawing.RichElement(self, element, data.elementID, data.username);
    }

    self.onjoin = function(data) {
        // connectionID, boardID, username, color
        self.colors[data.username] = data.color;
        noty({
            text: data.username + " joined whiteboard.",
            layout:"top",
            type:"information",
            animateOpen:{height:"toggle"},animateClose:{height:"toggle"},speed:500,timeout:5000,closeButton:false,closeOnSelfClick:true,closeOnSelfOver:false,modal:false});
    }

    self.onleave = function(data) {
        noty({
            text: data.username + " left whiteboard.",
            layout:"top",
            type:"information",
            animateOpen:{height:"toggle"},animateClose:{height:"toggle"},speed:500,timeout:5000,closeButton:false,closeOnSelfClick:true,closeOnSelfOver:false,modal:false});

    }

    self.onremove = function(id) {
        console.log("removing " + id);
        var elem = $('#' + id);
        console.log("removing " + id + " -> " + elem);
        if (elem)
            Drawing.Remove(elem);
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

    var connURL = "ws://" + document.location.host + "/whiteboard/interface?u=" + encodeURI(username) + "&t=" + encodeURI(hashID);

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

    document.onselectstart = function() {
        return false;
    }
    $('section').hide();
    $('#whiteboard').show();

    console.log("username=" + username + ", hash=" + hashID);

    app = new App.Controller(hashID, 'canvas');

    var pencil = new Tools.Pencil(app);
    var line  = new Tools.Line(app);
    var rectangle = new Tools.Rectangle(app);
    var eraser = new Tools.Eraser(app);

    app.tool(pencil);
    app.client = new App.WhiteboardClient(app, hashID, username);

    // init dialog.
    $('#imageupload').dialog({
        autoOpen: false,
        height: 200,
        width: '50%',
        modal: true
    });

    $('#icontool').dialog({
        autoOpen: false,
        height: 300,
        width: '50%',
        modal: true
    });

    var toolSetup = function(tool, elem) {
        $('.tool').removeClass('isActive');
        $(elem).addClass('isActive');
        app.tool(tool);
    }

    $('#icontool img').click(function() {
        toolSetup(new Tools.Image(app, $(this).attr('src')), $('#icons'));
        $('#icontool').dialog('close');
    });

    $('#texttool').dialog({
        autoOpen: false,
        height: 200,
        width: '50%',
        modal: true,
        buttons: {
            "Create": function() {
                toolSetup(app.tool(new Tools.Text(app, $('#textInput').val())), $('#image'));
                $(this).dialog('close');
            },
            "Cancel": function() {
                $(this).dialog('close');
            }
        }
    });

    $('#imageUploadInput').change(function(evt) {
        uploadFile("resource/upload?t=" + encodeURI(hashID), evt.target.files[0], function(data) {
            console.log("url = " + data.url);
            app.tool(new Tools.Image(app, data.url));
            $('#imageupload').dialog("close");
        });
    });

    // init colors
    $.each(['black','red','green','blue','yellow', 'white'], function(idx, val) {
        console.log("setting color " + val);
        $('.' + val)
            .css('background-color', val)
            .click(function() {
                app.color(val);
            });
    });

    // hook up tools
    $('#line').click(function(){ toolSetup(line, this); });
    $('#rectangle').click(function(){ toolSetup(rectangle, this);  });
    $('#pencil').click(function(){ toolSetup(pencil, this);  });
    $('#eraser').click(function(){ toolSetup(eraser, this); });
    $('#snapshot').click(function(){ Drawing.Snapshot(app) });
    $('#image').click(function() {
        $('#imageupload').dialog("open");
    });
    $('#text').click(function() {
        $('#texttool').dialog('open');
    })
    $('#icons').click(function() {
        $('#icontool').dialog('open');
    })
}

function activateWelcome(username) {

    $('section').hide();
    $('#create').show();

    var createInput = $('#createInput');
    $.ajax({url: 'scrum/stories', dataType: 'json'}).done(function(data){
        for (var val in data) {
            $('<option>').text(data[val].storyTitle)
                .val(data[val].storyTitle)
                .data('description', data[val].storyDescription)
                .appendTo(createInput);
        }
    });

    $('#createButton').click(function() {
        $.ajax({
            url: '/whiteboard/create',
            dataType: 'json',
            data: {'t': $('#createInput').val(), 'u': $('#username').text(), 'd': $('#createInput').data('description')}
        }).done(function(data) {
            var id = data.boardID;
            window.location.hash = '!' + id;
            activateWhiteboard($('#username').val(), encodeURI(id));
        });
    });
}

function activateJoin(username) {

    $('section').hide();
    $('#join').show();

    $('#username').text(username);
    $('#username').bind('click', function() {
        var self = $(this);
        var username = self.text();
        self.html('<input type="text" size="15" id="usernameInput"/>');
        $('#usernameInput').val(username);
        $('#usernameInput').change(function() {
            self.html($(this).val());
            localStorage.setItem("whiteboard-username", $(this).val());
        })
        $('#usernameInput').focus();
    })

    var joinInput = $('#joinInput');
    $.ajax({url: 'whiteboard/list', dataType: 'json'}).done(function(data){
        $.each(data.boardIDs, function(idx, val) {
            $('<option>').text(val)
                .val(val)
                .data('description', val)
                .appendTo(joinInput);
        });
    });

    $('#joinButton').click(function() {
        var id = $('#joinInput').val();
        window.location.hash = '!' + id;
        activateWhiteboard($('#username').text(), encodeURI(id));
    });
}

$(document).ready(function () {

    var username = localStorage.getItem("whiteboard-username") || Math.random().toString(36).substr(2, 6);
    var hashID = window.location.hash.substring(2);

    // if we have hashID then show whiteboard, otherwise show welcome screen
    if (hashID == "") {
        activateJoin(username);
    }
    else {
        activateWhiteboard(username, hashID);
    }

});
