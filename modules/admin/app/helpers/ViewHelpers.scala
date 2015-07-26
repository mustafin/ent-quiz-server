package helpers

import views.html.admin.templates.textConst
import views.html.helper.FieldConstructor
/**
 * Created by Murat.
 */
object ViewHelpers {

  implicit val myFields = FieldConstructor(textConst.f)

}
