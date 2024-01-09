// Inicializar Firestore
var db = firebase.firestore();


function ingresar(event) {
  event.preventDefault(); // prevent the form from submitting
  
  var email2 = document.getElementById('email').value;
  var password3 = document.getElementById('password').value;
  
  firebase.auth().signInWithEmailAndPassword(email2, password3)
    .then(function () {
      console.log("Usuario ingresado con éxito!");
      alert("Usuario ingresado con éxito!");
      window.location.href = "../cuidadores/cuidadores.html";
    })
    .catch(function (error) {
      console.error("Error al ingresar el usuario: ", error);
      alert("Error al ingresar el usuario");
    });
}

document.addEventListener('DOMContentLoaded', function() {
  var checkbox1 = document.getElementById('checkbox1');
  var checkbox2 = document.getElementById('checkbox2');
  var emailInput = document.querySelector('.input[type="email"]');
  var signUpButton = document.querySelector('.btn');
  
  checkbox1.addEventListener('change', function() {
    if (checkbox1.checked) {
      checkbox2.checked = false; // Desmarca checkbox2 si checkbox1 está seleccionado
    }
  });
  
  checkbox2.addEventListener('change', function() {
    if (checkbox2.checked) {
      checkbox1.checked = false; // Desmarca checkbox1 si checkbox2 está seleccionado
    }
  });
  
  signUpButton.addEventListener('click', function(event) {
    event.preventDefault();
  
    var emailValue = emailInput.value;
    var role = '';
  
    if (checkbox1.checked) {
      emailValue = 'cuidador' + emailValue;
      role = 'Cuidador';
    } else if (checkbox2.checked) {
      emailValue = 'dueño' + emailValue;
      role = 'Dueño';
    }
  
    // Guardar la información en Firebase Authentication
    firebase.auth().createUserWithEmailAndPassword(emailValue, '123456789')
      .then(function(userCredential) {
        // Obtener el ID del usuario creado
        var userId = userCredential.user.uid;
        
        // Actualizar el perfil del usuario con el rol
        userCredential.user.updateProfile({
          displayName: role
        })
        .then(function() {
          console.log("Perfil actualizado con éxito");
          alert("Información guardada con éxito en Firebase Authentication.");
          
          // Guardar la información adicional en Firestore
          db.collection('usuarios').doc(userId).set({
            email: emailValue,
            role: role
          })
          .then(function() {
            console.log("Información guardada con éxito en Firestore.");
            // Restablecer los valores de los campos del formulario si es necesario
          })
          .catch(function(error) {
            console.error("Error al guardar la información en Firestore: ", error);
            alert("Error al guardar la información en Firestore");
          });
        })
        .catch(function(error) {
          console.error("Error al actualizar el perfil: ", error);
          alert("Error al guardar la información en Firebase Authentication");
        });
      })
      .catch(function(error) {
        console.error("Error al crear el usuario: ", error);
        alert("Error al guardar la información en Firebase Authentication");
      });
  });
});


const signInBtn = document.getElementById("signIn");
const signUpBtn = document.getElementById("signUp");
const fistForm = document.getElementById("form1");
const secondForm = document.getElementById("form2");
const container = document.querySelector(".container");

signInBtn.addEventListener("click", () => {
	container.classList.remove("right-panel-active");
});

signUpBtn.addEventListener("click", () => {
	container.classList.add("right-panel-active");
});

fistForm.addEventListener("submit", (e) => e.preventDefault());
secondForm.addEventListener("submit", (e) => e.preventDefault());



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




// seleccionar los elementos del menú "Servicios", "Ubicación" y "Perfil"
var serviciosItem = document.querySelector("#menu a[href='../servicios/servicios.html']");
var ubicacionItem = document.querySelector("#menu a[href='../ubicacion/mapa.html']");
var perfilItem = document.querySelector("#menu a[href='../cuidadores/cuidadores.html']");
var contactoItem = document.querySelector("#menu a[href='../contacto/contacto.html']");

// desactivar los elementos del menú eliminando sus atributos href
serviciosItem.removeAttribute("href");
ubicacionItem.removeAttribute("href");
perfilItem.removeAttribute("href");
contactoItem.removeAttribute("href");

// aplicar estilo de elemento deshabilitado
serviciosItem.style.color = "rgb(255, 215, 0)";
serviciosItem.style.cursor = "default";
serviciosItem.style.textDecoration = "none";

ubicacionItem.style.color = "rgb(255, 215, 0)";
ubicacionItem.style.cursor = "default";
ubicacionItem.style.textDecoration = "none";

perfilItem.style.color = "rgb(255, 215, 0)";
perfilItem.style.cursor = "default";
perfilItem.style.textDecoration = "none";

contactoItem.style.color = "rgb(255, 215, 0)";
contactoItem.style.cursor = "default";
contactoItem.style.textDecoration = "none";

function setNextCardLeft() {
	if (currentPerson === 3) {
		currentPerson = 0;
		slide("left", currentPerson);
	} else {
		currentPerson++;
	}

	slide("left", currentPerson);
}

function setNextCardRight() {
	if (currentPerson === 0) {
		currentPerson = 3;
		slide("right", currentPerson);
	} else {
		currentPerson--;
	}

	slide("right", currentPerson);
}

leftArrow.addEventListener("click", setNextCardLeft);
rightArrow.addEventListener("click", setNextCardRight);

surpriseMeBtn.addEventListener("click", () => {
  window.location.href = "../login/login.html";
});

window.addEventListener("resize", () => {
	description.style.height = "100%";
});

