function deleteFile(path, filename) {
    const form = document.forms.deleteForm
    form.elements.path.value = path
    form.elements.filename.value = filename
    form.submit()
}
