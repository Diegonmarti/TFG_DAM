// Inicializar Firestore
var db = firebase.firestore();
const darkModeToggle = document.querySelector('#dark-mode-toggle');

darkModeToggle.addEventListener('change', () => {
  document.body.classList.toggle('dark-mode');
});

function registrar() {
  var email = document.getElementById('email').value;
  var usuario = document.getElementById('usuario').value;
  var numero = document.getElementById('numero').value;
  var contrasena = document.getElementById('contrasena').value;
  var repcontrasena = document.getElementById('repcontraseña').value;

    if (email === "" || usuario === "" || numero === "" || contrasena === "" || repcontrasena === "") {
      alert("Por favor, complete todos los campos.");
      return;
    }
  
    if (contrasena == repcontrasena) {
      firebase.auth().createUserWithEmailAndPassword(email, contrasena)
        .then(function () {
          db.collection("users").doc(usuario).set({
            email: email,
            usuario: usuario,
            numero: numero
          })
          .then(function () {
            console.log("Usuario registrado con éxito!");
            alert("Usuario registrado con éxito!");
          })
          .catch(function (error) {
            console.error("Error al guardar los datos del usuario: ", error);
            alert("Error al guardar los datos del usuario");
          });
        })
        .catch(function (error) {
          console.error("Error al registrar el usuario: ", error);
          alert("Error al registrar el usuario");
        });
    } else {
      alert("Las contraseñas no coinciden");
    }
  }
  

function ingresar() {
  var email2 = document.getElementById('email2').value;
  var password3 = document.getElementById('password3').value;

  firebase.auth().signInWithEmailAndPassword(email2, password3)
    .then(function () {
      console.log("Usuario ingresado con éxito!");
      alert("Usuario ingresado con éxito!");
    })
    .catch(function (error) {
      console.error("Error al ingresar el usuario: ", error);
      alert("Error al ingresar el usuario");
    });
}


const acceptTermsCheckbox = document.getElementById('accept-terms');
const acceptButton = document.getElementById('popup-btn');

acceptTermsCheckbox.addEventListener('change', function() {
  if (this.checked) {
    acceptButton.disabled = false;
  } else {
    acceptButton.disabled = true;
  }
});

document.getElementById("popup-btn").addEventListener("click", function() {
  document.getElementById("popup").style.display = "none";
});
document.getElementById("close-btn").addEventListener("click", function() {
  document.getElementById("popup").style.display = "none";
});

function onSubmit(token) {
  console.log('El token ha sido verificado y se ha enviado a tu servidor');
}

grecaptcha.execute('6LeClW0lAAAAAMP2y3TgBS9fzUWA-TV1Z7Dmp3Kc', {action: 'submit'}).then(function(token) {
  onSubmit(token);
});

