function addCart(form) {

    const formData = new FormData(form);

    fetch(form.action, {
        method: 'POST',
        body: formData
    })
    .then(response => {

        if (!response.ok) {
            throw new Error("No se pudo agregar el producto al carrito.");
        }

        return response.text();
    })
    .then(() => {

        window.location.href = "/carrito/listado";

    })
    .catch(error => {

        console.error(
            "Error al agregar al carrito:",
            error
        );

        alert(
            "Ocurrió un error al agregar el producto al carrito."
        );

    });

    return false;
}


