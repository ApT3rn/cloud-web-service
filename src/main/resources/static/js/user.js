function fillFormValuesDeleteFile(path, filename) {
    const form = document.forms.deleteFileForm
    form.elements.path.value = path
    form.elements.filename.value = filename
    form.submit()
}

function fillFormValuesRenameFile(filename) {
    const form = document.forms.renameFileForm
    form.elements.filename.value = filename
}
