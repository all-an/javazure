document.addEventListener('DOMContentLoaded', function() {
    const messageForm = document.getElementById('messageForm');
    const customAlert = document.getElementById('customAlert');
    const alertIcon = document.getElementById('alertIcon');
    const alertTitle = document.getElementById('alertTitle');
    const alertMessage = document.getElementById('alertMessage');
    const alertButton = document.getElementById('alertButton');
    
    // Custom alert function
    function showCustomAlert(title, message, isSuccess = true) {
        alertIcon.textContent = isSuccess ? '✅' : '❌';
        alertTitle.textContent = title;
        alertMessage.textContent = message;
        alertButton.textContent = isSuccess ? 'Awesome!' : 'Got it';
        
        // Show the alert
        customAlert.classList.add('show');
        
        // Focus on button for accessibility
        setTimeout(() => alertButton.focus(), 100);
    }
    
    // Close alert when button is clicked or outside is clicked
    alertButton.addEventListener('click', function() {
        customAlert.classList.remove('show');
    });
    
    customAlert.addEventListener('click', function(e) {
        if (e.target === customAlert) {
            customAlert.classList.remove('show');
        }
    });
    
    // Close alert with Escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && customAlert.classList.contains('show')) {
            customAlert.classList.remove('show');
        }
    });
    
    if (messageForm) {
        messageForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const content = document.getElementById('content').value;
            const author = document.getElementById('author').value;
            const submitButton = e.target.querySelector('button[type="submit"]');
            
            if (!content.trim()) {
                showCustomAlert('Oops!', 'Please enter a message before sending.', false);
                return;
            }
            
            // Show loading state
            const originalText = submitButton.textContent;
            submitButton.textContent = 'Sending...';
            submitButton.disabled = true;
            
            fetch('/messages', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    content: content.trim(),
                    author: author.trim() || null
                })
            })
            .then(response => response.json())
            .then(data => {
                // Clear form
                document.getElementById('content').value = '';
                document.getElementById('author').value = '';
                
                // Show success alert with server message
                showCustomAlert('Success!', data.message || 'Message sent successfully!', true);
            })
            .catch(error => {
                console.error('Error:', error);
                showCustomAlert('Error', 'Something went wrong. Please try again.', false);
            })
            .finally(() => {
                // Reset button state
                submitButton.textContent = originalText;
                submitButton.disabled = false;
            });
        });
    }
});