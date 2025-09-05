document.addEventListener('DOMContentLoaded', function() {
    const messageForm = document.getElementById('messageForm');
    
    if (messageForm) {
        messageForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const content = document.getElementById('content').value;
            const author = document.getElementById('author').value;
            const submitButton = e.target.querySelector('button[type="submit"]');
            
            if (!content.trim()) {
                alert('Please enter a message');
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
                alert(data.message || 'Message sent successfully!');
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error posting message. Please try again.');
            })
            .finally(() => {
                // Reset button state
                submitButton.textContent = originalText;
                submitButton.disabled = false;
            });
        });
    }
});