function fillFormValuesDeleteFile(path, filename) {
    const form = document.forms.deleteFileForm //Создаём переменную нашей формы "deleteFileForm"
    form.elements.path.value = path //Присваем значение "path" в форме
    form.elements.filename.value = filename //Присваем значение "filename" в форме
    form.submit() //Запускаем форму по кнопке
}

function fillFormValuesRenameFile(filename) {
    const form = document.forms.renameFileForm
    form.elements.filename.value = filename
}

function fillFormValuesSharedFile(path, filename) {
    const form = document.forms.shared
    form.elements.path.value = path //Присваем значение "path" в форме
    form.elements.filename.value = filename //Присваем значение "filename" в форме
}
