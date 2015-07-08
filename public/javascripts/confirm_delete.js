/**
 * Created by Murat on 06.07.2015.
 */

$('#confirmDelete').on('show.bs.modal', function (e) {
    $message = $(e.relatedTarget).attr('data-message');
    $(this).find('.modal-body p').text($message);
    $title = $(e.relatedTarget).attr('data-title');
    $(this).find('.modal-title').text($title);

    $(this).find('#confirm').attr('href', $(e.relatedTarget).data('href'));
});
