## Облачный сервис хранения файлов.
<br>

![2023-05-28_18-35-30](https://github.com/ApT3rn/cloud/assets/96689510/6a35acd0-f023-42b2-8da7-3b7e163ce034)
<p align=center>Главная страница</p>
<br>

Сервис хранения файлов для пользователей, имеет возможность
регистрации/авторизации пользователей, после чего его перенаправляет
в личный кабинет где у пользователя есть возможность загрузить свои файлы, а
управлять ими: переименовывать, делиться по ссылке, удалять. По мимо этого
он может воспользоваться поиском и найти необходимый ему файл, также он может 
просматривать свои файлы по категориям: изображения, видео, музыка, документы, а также
файлы, которыми он поделился по ссылке.
<br>

![2023-06-01_00-55-16](https://github.com/ApT3rn/cloud/assets/96689510/ad09614a-296f-46a4-a974-23ddd0b5b71f)
<p align=center>Страница личного кабинета</p>

### Стек:

Java 8, Spring Framework(Boot, Web, Data, Security), Maven, Lombok, Flyway, PostgreSQL, Thyameleaf, HTML, CSS.

### Инстукция для запуска:

Для начала перейдите по пути /src/main/resources/, после чего откройте файл application.properties 
и заполните свои значения в параметрах для подключения к базе данных:

<p>spring.datasource.url=</p>
<p>spring.datasource.username=</p>
<p>spring.datasource.password=</p>

<br>

После перейдите в директорию /resources/statis/js/ и откройте файл user.js, 
там необходимо заполнить переменную «projectUrl» вашим url адресом, пример:

<p>var projectUrl = "http://localhost:8080/"</p>

<br>

После этих действий приложение готово к запуску.

###
