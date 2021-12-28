import os
import app as app
from flask import Flask, request, flash
from werkzeug.utils import secure_filename, redirect

UPLOAD_FOLDER = 'C:\\Users\\Dell\\Desktop\\MC Video'
app = Flask(__name__)

app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


@app.route('/uploader', methods=['GET', 'POST'])
def handle_request():
    if request.method == 'POST':
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']
        if file:
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            return "Success"
    else:
        return "Welcome to Janam's Flask server"


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=9000, debug=True)