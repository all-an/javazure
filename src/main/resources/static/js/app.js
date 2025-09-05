document.addEventListener('DOMContentLoaded', function() {
    const messageForm = document.getElementById('messageForm');
    
    if (messageForm) {
        messageForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const content = document.getElementById('content').value;
            const author = document.getElementById('author').value;
            
            if (!content.trim()) {
                alert('Please enter a message');
                return;
            }
            
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
                
                // Show success alert
                alert('Message sent successfully!');
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error posting message. Please try again.');
            });
        });
    }
});