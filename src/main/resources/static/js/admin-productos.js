document.addEventListener('DOMContentLoaded', () => {
    const modal = document.querySelector('#productModal');
    const search = document.querySelector('#adminProductSearch');
    const imageInput = document.querySelector('#productImageInput');
    const imageUploadText = document.querySelector('#imageUploadText');

    document.querySelector('[data-open-product-modal]')?.addEventListener('click', () => {
        modal?.classList.add('is-open');
        modal?.setAttribute('aria-hidden', 'false');
    });

    document.querySelectorAll('[data-close-product-modal]').forEach((button) => {
        button.addEventListener('click', () => cerrarModal(modal));
    });

    modal?.addEventListener('click', (event) => {
        if (event.target === modal) {
            cerrarModal(modal);
        }
    });

    search?.addEventListener('input', () => {
        const query = search.value.trim().toLowerCase();

        document.querySelectorAll('[data-product-name]').forEach((row) => {
            row.style.display = row.dataset.productName.includes(query) ? '' : 'none';
        });
    });

    imageInput?.addEventListener('change', () => {
        imageUploadText.textContent = imageInput.files?.[0]?.name || 'Cargar imagen';
    });
});

function cerrarModal(modal) {
    modal?.classList.remove('is-open');
    modal?.setAttribute('aria-hidden', 'true');
}
