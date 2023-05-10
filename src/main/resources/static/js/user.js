function fillFormValuesDeleteFile(path, filename) {
    const form = document.forms.deleteFileForm //Создаём переменную нашей формы "deleteFileForm"
    form.elements.path.value = path //Присваем значение "path" в форме
    form.elements.filename.value = filename //Присваем значение "filename" в форме
    form.submit() //Запускаем форму по кнопке
}

function fillFormValuesRenameFile(filename, type) {
    const form = document.forms.renameFileForm
    form.elements.filename.value = filename
    form.elements.type.value = type
}

function fillFormValuesSharedFile(path, filename, id) {
    const form = document.forms.shared
    form.elements.path.value = path //Присваем значение "path" в форме
    form.elements.filename.value = filename //Присваем значение "filename" в форме
    form.elements.id.value = id
    if (id) {
        form.elements.idurl.value = "http://datasky.space/file/" + id
        document.getElementById("sub").innerText = "Закрыть доступ по ссылке"
    } else {
        document.getElementById("sub").innerText = "Открыть доступ по ссылке"
    }
}
