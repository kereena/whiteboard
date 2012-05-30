/**
 * Public domain. upload a file using XMLHttpRequest.
 */
function uploadFile(url, file, callback) {

    if (!(file instanceof File))
        throw new Error("Not a File passed");

    var name = file.fileName != null ? file.fileName : file.name;

    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function(){
        if (xhr.readyState == 4) {
            console.log("response = " + xhr.responseText);
            callback(JSON.parse(xhr.responseText));
        }
    };
    xhr.open("POST", url, true);
    xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xhr.setRequestHeader("X-File-Name", encodeURIComponent(name));
    xhr.setRequestHeader("X-Content-Type", file.type);
    xhr.send(file);
}